package valens.example.task_flow.messaging.events;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class TaskCreatedEvent extends BaseEvent {
    private UUID taskId;
    private String title;
    private UUID reporterId;
    private UUID assigneeId;
    private String priority;
    private Instant dueDate;
    private Instant createdAt;

    public TaskCreatedEvent() {
        super("TaskCreated");
    }
}

