package valens.example.task_flow.tasks.dto;

import jakarta.validation.constraints.Size;
import valens.example.task_flow.tasks.entity.Priority;
import valens.example.task_flow.tasks.entity.TaskStatus;

import java.time.LocalDate;
import java.util.UUID;

public class UpdateTaskRequest {

    @Size(max = 200)
    private String title;

    @Size(max = 2000)
    private String description;

    private TaskStatus status;

    private UUID assigneeId;

    private LocalDate dueDate;

    private Priority priority;

    // Constructors
    public UpdateTaskRequest() {
    }

    public UpdateTaskRequest(String title, String description, TaskStatus status, UUID assigneeId, LocalDate dueDate,
            Priority priority) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.assigneeId = assigneeId;
        this.dueDate = dueDate;
        this.priority = priority;
    }

    // Getters and Setters
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

    public UUID getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(UUID assigneeId) {
        this.assigneeId = assigneeId;
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
}
