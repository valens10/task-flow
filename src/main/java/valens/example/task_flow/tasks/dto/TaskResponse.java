package valens.example.task_flow.tasks.dto;

import valens.example.task_flow.tasks.entity.Priority;
import valens.example.task_flow.tasks.entity.TaskStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class TaskResponse {

    private UUID id;
    private String title;
    private String description;
    private TaskStatus status;
    private UserInfo assignee;
    private UserInfo reporter;
    private LocalDate dueDate;
    private Priority priority;
    private Instant createdAt;
    private Instant updatedAt;
    private long commentCount;

    // Constructors
    public TaskResponse() {
    }

    public TaskResponse(UUID id, String title, String description, TaskStatus status,
            UserInfo assignee, UserInfo reporter, LocalDate dueDate,
            Priority priority, Instant createdAt, Instant updatedAt, long commentCount) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.assignee = assignee;
        this.reporter = reporter;
        this.dueDate = dueDate;
        this.priority = priority;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.commentCount = commentCount;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public UserInfo getAssignee() {
        return assignee;
    }

    public void setAssignee(UserInfo assignee) {
        this.assignee = assignee;
    }

    public UserInfo getReporter() {
        return reporter;
    }

    public void setReporter(UserInfo reporter) {
        this.reporter = reporter;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
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

    public long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(long commentCount) {
        this.commentCount = commentCount;
    }

    // Nested UserInfo class
    public static class UserInfo {
        private UUID id;
        private String email;
        private String fullName;

        public UserInfo() {
        }

        public UserInfo(UUID id, String email, String fullName) {
            this.id = id;
            this.email = email;
            this.fullName = fullName;
        }

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }
    }
}
