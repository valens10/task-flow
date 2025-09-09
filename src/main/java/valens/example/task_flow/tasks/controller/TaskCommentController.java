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
import valens.example.task_flow.tasks.dto.TaskCommentRequest;
import valens.example.task_flow.tasks.dto.TaskCommentResponse;
import valens.example.task_flow.tasks.service.TaskCommentService;
import valens.example.task_flow.users.entity.User;

import java.util.UUID;

@RestController
@RequestMapping("/api/tasks/{taskId}/comments")
@Tag(name = "Task Comments", description = "Task comment management endpoints")
public class TaskCommentController {

    private final TaskCommentService taskCommentService;

    public TaskCommentController(TaskCommentService taskCommentService) {
        this.taskCommentService = taskCommentService;
    }

    @PostMapping
    @Operation(summary = "Add a comment to a task")
    public ResponseEntity<TaskCommentResponse> createComment(
            @Parameter(description = "Task ID") @PathVariable UUID taskId,
            @Valid @RequestBody TaskCommentRequest request,
            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        TaskCommentResponse comment = taskCommentService.createComment(taskId, request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    @GetMapping
    @Operation(summary = "Get comments for a task")
    public ResponseEntity<Page<TaskCommentResponse>> getCommentsForTask(
            @Parameter(description = "Task ID") @PathVariable UUID taskId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<TaskCommentResponse> comments = taskCommentService.getCommentsForTask(taskId, pageable);
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/{commentId}")
    @Operation(summary = "Update a comment")
    public ResponseEntity<TaskCommentResponse> updateComment(
            @Parameter(description = "Task ID") @PathVariable UUID taskId,
            @Parameter(description = "Comment ID") @PathVariable Long commentId,
            @Valid @RequestBody TaskCommentRequest request,
            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        TaskCommentResponse comment = taskCommentService.updateComment(commentId, request, currentUser);
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "Delete a comment")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "Task ID") @PathVariable UUID taskId,
            @Parameter(description = "Comment ID") @PathVariable Long commentId,
            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        taskCommentService.deleteComment(commentId, currentUser);
        return ResponseEntity.noContent().build();
    }
}

@RestController
@RequestMapping("/api/comments")
@Tag(name = "Comments", description = "Comment management endpoints")
class CommentController {

    private final TaskCommentService taskCommentService;

    public CommentController(TaskCommentService taskCommentService) {
        this.taskCommentService = taskCommentService;
    }

    @GetMapping("/my-comments")
    @Operation(summary = "Get comments by current user")
    public ResponseEntity<Page<TaskCommentResponse>> getMyComments(
            Authentication authentication,
            @PageableDefault(size = 20) Pageable pageable) {
        User currentUser = (User) authentication.getPrincipal();
        Page<TaskCommentResponse> comments = taskCommentService.getCommentsByAuthor(currentUser, pageable);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/search")
    @Operation(summary = "Search comments by content")
    public ResponseEntity<Page<TaskCommentResponse>> searchComments(
            @Parameter(description = "Search term") @RequestParam String q,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<TaskCommentResponse> comments = taskCommentService.searchComments(q, pageable);
        return ResponseEntity.ok(comments);
    }
}
