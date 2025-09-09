package valens.example.task_flow.tasks.dto;

import java.time.Instant;

public class TaskCommentResponse {

    private Long id;
    private String body;
    private TaskResponse.UserInfo author;
    private Instant createdAt;
    private Instant updatedAt;

    // Constructors
    public TaskCommentResponse() {
    }

    public TaskCommentResponse(Long id, String body, TaskResponse.UserInfo author,
            Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.body = body;
        this.author = author;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public TaskResponse.UserInfo getAuthor() {
        return author;
    }

    public void setAuthor(TaskResponse.UserInfo author) {
        this.author = author;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
