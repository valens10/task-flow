package valens.example.task_flow.tasks.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import valens.example.task_flow.tasks.dto.*;
import valens.example.task_flow.tasks.entity.TaskStatus;
import valens.example.task_flow.tasks.entity.Priority;
import valens.example.task_flow.tasks.service.TaskService;
import valens.example.task_flow.users.entity.User;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "Task management endpoints")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @Operation(summary = "Create a new task")
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody CreateTaskRequest request,
            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        TaskResponse task = taskService.createTask(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID")
    public ResponseEntity<TaskResponse> getTask(
            @Parameter(description = "Task ID") @PathVariable UUID id) {
        TaskResponse task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update task")
    public ResponseEntity<TaskResponse> updateTask(
            @Parameter(description = "Task ID") @PathVariable UUID id,
            @Valid @RequestBody UpdateTaskRequest request) {
        TaskResponse task = taskService.updateTask(id, request);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task")
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "Task ID") @PathVariable UUID id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Get all tasks with pagination and filtering")
    public ResponseEntity<PagedResponse<TaskResponse>> getAllTasks(
            @Parameter(description = "Assignee ID") @RequestParam(required = false) UUID assigneeId,
            @Parameter(description = "Reporter ID") @RequestParam(required = false) UUID reporterId,
            @Parameter(description = "Task status") @RequestParam(required = false) TaskStatus status,
            @Parameter(description = "Task priority") @RequestParam(required = false) Priority priority,
            @Parameter(description = "Due date from") @RequestParam(required = false) LocalDate dueDateFrom,
            @Parameter(description = "Due date to") @RequestParam(required = false) LocalDate dueDateTo,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<TaskResponse> tasks;

        if (assigneeId != null || reporterId != null || status != null || priority != null ||
                dueDateFrom != null || dueDateTo != null) {
            // For now, use basic filtering - in a real app, you'd fetch User entities
            tasks = taskService.getAllTasks(pageable);
        } else {
            // Get all tasks
            tasks = taskService.getAllTasks(pageable);
        }

        return ResponseEntity.ok(new PagedResponse<>(tasks));
    }

    @GetMapping("/my-tasks")
    @Operation(summary = "Get tasks for current user")
    public ResponseEntity<Page<TaskResponse>> getMyTasks(
            Authentication authentication,
            @PageableDefault(size = 20) Pageable pageable) {
        User currentUser = (User) authentication.getPrincipal();
        Page<TaskResponse> tasks = taskService.getTasksForUser(currentUser, pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/assigned-to-me")
    @Operation(summary = "Get tasks assigned to current user")
    public ResponseEntity<Page<TaskResponse>> getTasksAssignedToMe(
            Authentication authentication,
            @PageableDefault(size = 20) Pageable pageable) {
        User currentUser = (User) authentication.getPrincipal();
        Page<TaskResponse> tasks = taskService.getTasksByAssignee(currentUser, pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/reported-by-me")
    @Operation(summary = "Get tasks reported by current user")
    public ResponseEntity<Page<TaskResponse>> getTasksReportedByMe(
            Authentication authentication,
            @PageableDefault(size = 20) Pageable pageable) {
        User currentUser = (User) authentication.getPrincipal();
        Page<TaskResponse> tasks = taskService.getTasksByReporter(currentUser, pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get tasks by status")
    public ResponseEntity<Page<TaskResponse>> getTasksByStatus(
            @Parameter(description = "Task status") @PathVariable TaskStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<TaskResponse> tasks = taskService.getTasksByStatus(status, pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/priority/{priority}")
    @Operation(summary = "Get tasks by priority")
    public ResponseEntity<Page<TaskResponse>> getTasksByPriority(
            @Parameter(description = "Task priority") @PathVariable Priority priority,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<TaskResponse> tasks = taskService.getTasksByPriority(priority, pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue tasks")
    public ResponseEntity<Page<TaskResponse>> getOverdueTasks(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<TaskResponse> tasks = taskService.getOverdueTasks(pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/due-today")
    @Operation(summary = "Get tasks due today")
    public ResponseEntity<Page<TaskResponse>> getTasksDueToday(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<TaskResponse> tasks = taskService.getTasksDueToday(pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/search")
    @Operation(summary = "Search tasks by title or description")
    public ResponseEntity<Page<TaskResponse>> searchTasks(
            @Parameter(description = "Search term") @RequestParam String q,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<TaskResponse> tasks = taskService.searchTasks(q, pageable);
        return ResponseEntity.ok(tasks);
    }
}
