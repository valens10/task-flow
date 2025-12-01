package valens.example.task_flow.messaging.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * Base event class with common metadata for all domain events.
 * Includes trace context for observability correlation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "eventType")
@JsonSubTypes({
    @JsonSubTypes.Type(value = TaskCreatedEvent.class, name = "TaskCreated"),
    @JsonSubTypes.Type(value = TaskUpdatedEvent.class, name = "TaskUpdated"),
    @JsonSubTypes.Type(value = TaskDeletedEvent.class, name = "TaskDeleted"),
    @JsonSubTypes.Type(value = UserRegisteredEvent.class, name = "UserRegistered"),
    @JsonSubTypes.Type(value = UserLoginEvent.class, name = "UserLogin")
})
public abstract class BaseEvent {
    private String eventId;
    private String eventType;
    private Instant timestamp;
    private String traceId;
    private String spanId;
    private Map<String, String> metadata;

    protected BaseEvent(String eventType) {
        this.eventType = eventType;
        this.eventId = java.util.UUID.randomUUID().toString();
        this.timestamp = Instant.now();
    }
}

