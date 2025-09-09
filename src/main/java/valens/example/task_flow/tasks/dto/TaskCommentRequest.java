package valens.example.task_flow.tasks.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TaskCommentRequest {

    @NotBlank
    @Size(max = 2000)
    private String body;

    // Constructors
    public TaskCommentRequest() {
    }

    public TaskCommentRequest(String body) {
        this.body = body;
    }

    // Getters and Setters
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
