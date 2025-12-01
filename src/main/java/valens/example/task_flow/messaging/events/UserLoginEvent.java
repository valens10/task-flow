package valens.example.task_flow.messaging.events;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserLoginEvent extends BaseEvent {
    private UUID userId;
    private String email;
    private Instant loginAt;
    private String ipAddress;

    public UserLoginEvent() {
        super("UserLogin");
    }
}

