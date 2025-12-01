package valens.example.task_flow.messaging.events;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class TaskDeletedEvent extends BaseEvent {
    private UUID taskId;
    private UUID deletedBy;

    public TaskDeletedEvent() {
        super("TaskDeleted");
    }
}

