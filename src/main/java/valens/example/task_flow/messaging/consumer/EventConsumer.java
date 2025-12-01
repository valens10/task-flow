package valens.example.task_flow.messaging.consumer;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import valens.example.task_flow.messaging.events.*;

/**
 * Kafka consumer for processing domain events.
 * Extracts trace context from Kafka headers and creates child spans.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EventConsumer {

    private final Tracer tracer;

    @KafkaListener(topics = "task-flow.task.created", groupId = "task-flow-consumer-group")
    public void handleTaskCreated(
            @Payload TaskCreatedEvent event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            ConsumerRecord<String, Object> record,
            Acknowledgment acknowledgment) {
        
        Span span = startConsumerSpan("kafka.consume.task.created", record);
        try {
            log.info("Processing TaskCreated event: taskId={}, title={}, traceId={}", 
                event.getTaskId(), event.getTitle(), event.getTraceId());
            
            // TODO: Implement business logic (e.g., send notification, update search index)
            // Example: notificationService.notifyAssignee(event.getAssigneeId(), event);
            
            acknowledgment.acknowledge();
            log.info("Successfully processed TaskCreated event: taskId={}", event.getTaskId());
        } catch (Exception e) {
            log.error("Error processing TaskCreated event: taskId={}", event.getTaskId(), e);
            throw e; // Error handler will retry or send to DLQ
        } finally {
            if (span != null) {
                span.end();
            }
            // Clean up MDC to prevent trace context leakage
            org.slf4j.MDC.clear();
        }
    }

    @KafkaListener(topics = "task-flow.task.updated", groupId = "task-flow-consumer-group")
    public void handleTaskUpdated(
            @Payload TaskUpdatedEvent event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            ConsumerRecord<String, Object> record,
            Acknowledgment acknowledgment) {
        
        Span span = startConsumerSpan("kafka.consume.task.updated", record);
        try {
            log.info("Processing TaskUpdated event: taskId={}, status={}, traceId={}", 
                event.getTaskId(), event.getStatus(), event.getTraceId());
            
            // TODO: Implement business logic
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Error processing TaskUpdated event: taskId={}", event.getTaskId(), e);
            throw e;
        } finally {
            if (span != null) {
                span.end();
            }
            // Clean up MDC to prevent trace context leakage
            org.slf4j.MDC.clear();
        }
    }

    @KafkaListener(topics = "task-flow.task.deleted", groupId = "task-flow-consumer-group")
    public void handleTaskDeleted(
            @Payload TaskDeletedEvent event,
            ConsumerRecord<String, Object> record,
            Acknowledgment acknowledgment) {
        
        Span span = startConsumerSpan("kafka.consume.task.deleted", record);
        try {
            log.info("Processing TaskDeleted event: taskId={}, deletedBy={}, traceId={}", 
                event.getTaskId(), event.getDeletedBy(), event.getTraceId());
            
            // TODO: Implement business logic (e.g., cleanup related data)
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Error processing TaskDeleted event: taskId={}", event.getTaskId(), e);
            throw e;
        } finally {
            if (span != null) {
                span.end();
            }
            // Clean up MDC to prevent trace context leakage
            org.slf4j.MDC.clear();
        }
    }

    @KafkaListener(topics = "task-flow.user.registered", groupId = "task-flow-consumer-group")
    public void handleUserRegistered(
            @Payload UserRegisteredEvent event,
            ConsumerRecord<String, Object> record,
            Acknowledgment acknowledgment) {
        
        Span span = startConsumerSpan("kafka.consume.user.registered", record);
        try {
            log.info("Processing UserRegistered event: userId={}, email={}, traceId={}", 
                event.getUserId(), event.getEmail(), event.getTraceId());
            
            // TODO: Implement business logic (e.g., send welcome email, analytics)
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Error processing UserRegistered event: userId={}", event.getUserId(), e);
            throw e;
        } finally {
            if (span != null) {
                span.end();
            }
            // Clean up MDC to prevent trace context leakage
            org.slf4j.MDC.clear();
        }
    }

    @KafkaListener(topics = "task-flow.user.login", groupId = "task-flow-consumer-group")
    public void handleUserLogin(
            @Payload UserLoginEvent event,
            ConsumerRecord<String, Object> record,
            Acknowledgment acknowledgment) {
        
        Span span = startConsumerSpan("kafka.consume.user.login", record);
        try {
            log.info("Processing UserLogin event: userId={}, email={}, traceId={}", 
                event.getUserId(), event.getEmail(), event.getTraceId());
            
            // TODO: Implement business logic (e.g., security audit, analytics)
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Error processing UserLogin event: userId={}", event.getUserId(), e);
            throw e;
        } finally {
            if (span != null) {
                span.end();
            }
            // Clean up MDC to prevent trace context leakage
            org.slf4j.MDC.clear();
        }
    }

    /**
     * Creates a child span for Kafka consumption, extracting trace context from headers.
     */
    private Span startConsumerSpan(String spanName, ConsumerRecord<String, Object> record) {
        // Extract trace context from Kafka headers
        String traceId = null;
        String spanId = null;
        
        if (record.headers() != null) {
            var traceIdHeader = record.headers().lastHeader("trace_id");
            var spanIdHeader = record.headers().lastHeader("span_id");
            
            if (traceIdHeader != null) {
                traceId = new String(traceIdHeader.value());
            }
            if (spanIdHeader != null) {
                spanId = new String(spanIdHeader.value());
            }
        }

        Span span = tracer.nextSpan()
            .name(spanName)
            .tag("kafka.topic", record.topic())
            .tag("kafka.partition", String.valueOf(record.partition()))
            .tag("kafka.offset", String.valueOf(record.offset()));

        if (traceId != null) {
            span = span.tag("trace_id", traceId);
        }

        span.start();
        
        // Set trace context in MDC for logging
        if (traceId != null) {
            org.slf4j.MDC.put("trace_id", traceId);
        }
        if (spanId != null) {
            org.slf4j.MDC.put("span_id", spanId);
        }

        return span;
    }
}

