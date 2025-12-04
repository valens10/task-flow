package valens.example.task_flow.messaging.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.RecordDeserializationException;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Global error handler for Kafka consumers.
 * Implements retry logic with exponential backoff and sends failed messages to DLQ.
 */
@Slf4j
@Component
public class KafkaErrorHandler implements CommonErrorHandler {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Map<TopicPartition, Integer> retryCount = new ConcurrentHashMap<>();

    public KafkaErrorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public boolean handleOne(Exception exception, ConsumerRecord<?, ?> record, 
                           Consumer<?, ?> consumer, MessageListenerContainer container) {
        log.error("Error processing message: topic={}, partition={}, offset={}, key={}, error={}", 
            record.topic(), record.partition(), record.offset(), record.key(), exception.getMessage(), exception);

        // For deserialization errors, skip immediately (retrying won't help)
        if (exception instanceof RecordDeserializationException || 
            exception instanceof SerializationException ||
            (exception.getCause() != null && 
             (exception.getCause() instanceof SerializationException))) {
            log.error("Deserialization error - skipping message and seeking past it");
            TopicPartition topicPartition = new TopicPartition(record.topic(), record.partition());
            consumer.seek(topicPartition, record.offset() + 1);
            return true; // Skip this message
        }

        // Track retries per topic-partition
        TopicPartition topicPartition = new TopicPartition(record.topic(), record.partition());
        int retries = retryCount.getOrDefault(topicPartition, 0);

        if (retries < 3) {
            // Retry with exponential backoff
            retryCount.put(topicPartition, retries + 1);
            long delayMs = (long) Math.pow(2, retries) * 1000; // 1s, 2s, 4s
            
            log.warn("Retrying message (attempt {}/3) after {}ms: topic={}, partition={}, offset={}", 
                retries + 1, delayMs, record.topic(), record.partition(), record.offset());
            
            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return true; // Skip on interrupt
            }
            
            return false; // Retry
        } else {
            // Max retries reached, send to DLQ
            retryCount.remove(topicPartition);
            sendToDlq(record, exception);
            return true; // Skip this message
        }
    }

    private void sendToDlq(ConsumerRecord<?, ?> record, Exception exception) {
        String dlqTopic = record.topic() + ".dlq";
        
        try {
            // Create DLQ message with original data + error info
            // Use HashMap instead of Map.of() to allow null values
            Map<String, Object> dlqMessage = new HashMap<>();
            dlqMessage.put("originalTopic", record.topic());
            dlqMessage.put("originalPartition", record.partition());
            dlqMessage.put("originalOffset", record.offset());
            dlqMessage.put("originalKey", record.key() != null ? record.key().toString() : null);
            dlqMessage.put("originalValue", record.value());
            dlqMessage.put("error", exception.getMessage() != null ? exception.getMessage() : "No error message");
            dlqMessage.put("errorClass", exception.getClass().getName());
            
            kafkaTemplate.send(dlqTopic, record.key() != null ? record.key().toString() : "null", dlqMessage);
            log.error("Sent failed message to DLQ: topic={}, originalTopic={}, partition={}, offset={}", 
                dlqTopic, record.topic(), record.partition(), record.offset());
        } catch (Exception e) {
            log.error("Failed to send message to DLQ: topic={}, originalTopic={}, error={}", 
                dlqTopic, record.topic(), e.getMessage(), e);
        }
    }

    @Override
    public void handleOtherException(Exception exception, Consumer<?, ?> consumer, 
                                   MessageListenerContainer container, boolean batchListener) {
        log.error("Kafka consumer error: {}", exception.getMessage(), exception);
    }
}

