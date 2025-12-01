package valens.example.task_flow.messaging.producer;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import valens.example.task_flow.messaging.events.BaseEvent;

import java.util.concurrent.CompletableFuture;

/**
 * Service for publishing domain events to Kafka with trace context propagation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Tracer tracer;

    /**
     * Publishes an event to Kafka with trace context in headers.
     * 
     * @param topic The Kafka topic name
     * @param key The message key (for partitioning)
     * @param event The event to publish
     */
    public void publish(String topic, String key, BaseEvent event) {
        try {
            // Inject trace context into event
            Span currentSpan = tracer.currentSpan();
            if (currentSpan != null) {
                event.setTraceId(currentSpan.context().traceId());
                event.setSpanId(currentSpan.context().spanId());
            }

            // Create producer record with trace headers
            ProducerRecord<String, Object> record = new ProducerRecord<>(topic, key, event);
            
            // Add trace context to Kafka headers for downstream propagation
            if (currentSpan != null) {
                record.headers().add("traceparent", 
                    String.format("00-%s-%s-01", 
                        currentSpan.context().traceId(),
                        currentSpan.context().spanId()).getBytes());
                record.headers().add("trace_id", currentSpan.context().traceId().getBytes());
                record.headers().add("span_id", currentSpan.context().spanId().getBytes());
            }

            // Publish asynchronously with callback
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(record);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Event published successfully: topic={}, key={}, eventType={}, eventId={}, traceId={}", 
                        topic, key, event.getEventType(), event.getEventId(), event.getTraceId());
                } else {
                    log.error("Failed to publish event: topic={}, key={}, eventType={}, eventId={}, error={}", 
                        topic, key, event.getEventType(), event.getEventId(), ex.getMessage(), ex);
                }
            });

        } catch (Exception e) {
            log.error("Error publishing event to topic {}: eventType={}, eventId={}, error={}", 
                topic, event.getEventType(), event.getEventId(), e.getMessage(), e);
            throw new RuntimeException("Failed to publish event: " + e.getMessage(), e);
        }
    }

    /**
     * Publishes an event without a key (Kafka will partition randomly).
     */
    public void publish(String topic, BaseEvent event) {
        publish(topic, null, event);
    }
}

