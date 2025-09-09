package valens.example.task_flow.tasks.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import valens.example.task_flow.tasks.dto.TaskCommentRequest;
import valens.example.task_flow.tasks.dto.TaskCommentResponse;
import valens.example.task_flow.tasks.dto.TaskResponse;
import valens.example.task_flow.tasks.entity.Task;
import valens.example.task_flow.tasks.entity.TaskComment;
import valens.example.task_flow.tasks.repository.TaskCommentRepository;
import valens.example.task_flow.tasks.repository.TaskRepository;
import valens.example.task_flow.users.entity.User;

import java.util.UUID;

@Service
@Transactional
public class TaskCommentService {

    private final TaskCommentRepository taskCommentRepository;
    private final TaskRepository taskRepository;

    public TaskCommentService(TaskCommentRepository taskCommentRepository, TaskRepository taskRepository) {
        this.taskCommentRepository = taskCommentRepository;
        this.taskRepository = taskRepository;
    }

    public TaskCommentResponse createComment(UUID taskId, TaskCommentRequest request, User author) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        TaskComment comment = new TaskComment();
        comment.setTask(task);
        comment.setAuthor(author);
        comment.setBody(request.getBody());

        TaskComment savedComment = taskCommentRepository.save(comment);
        return mapToTaskCommentResponse(savedComment);
    }

    @Transactional(readOnly = true)
    public Page<TaskCommentResponse> getCommentsForTask(UUID taskId, Pageable pageable) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        return taskCommentRepository.findByTaskOrderByCreatedAtDesc(task, pageable)
                .map(this::mapToTaskCommentResponse);
    }

    public TaskCommentResponse updateComment(Long commentId, TaskCommentRequest request, User user) {
        TaskComment comment = taskCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        // Check if user is the author of the comment
        if (!comment.getAuthor().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You can only edit your own comments");
        }

        comment.setBody(request.getBody());
        TaskComment updatedComment = taskCommentRepository.save(comment);
        return mapToTaskCommentResponse(updatedComment);
    }

    public void deleteComment(Long commentId, User user) {
        TaskComment comment = taskCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        // Check if user is the author of the comment
        if (!comment.getAuthor().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You can only delete your own comments");
        }

        comment.setDeleted(true);
        taskCommentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public Page<TaskCommentResponse> getCommentsByAuthor(User author, Pageable pageable) {
        return taskCommentRepository.findByAuthorOrderByCreatedAtDesc(author, pageable)
                .map(this::mapToTaskCommentResponse);
    }

    @Transactional(readOnly = true)
    public Page<TaskCommentResponse> searchComments(String search, Pageable pageable) {
        return taskCommentRepository.searchComments(search, pageable)
                .map(this::mapToTaskCommentResponse);
    }

    private TaskCommentResponse mapToTaskCommentResponse(TaskComment comment) {
        TaskResponse.UserInfo authorInfo = new TaskResponse.UserInfo(
                comment.getAuthor().getId(),
                comment.getAuthor().getEmail(),
                comment.getAuthor().getFullName());

        return new TaskCommentResponse(
                comment.getId(),
                comment.getBody(),
                authorInfo,
                comment.getCreatedAt(),
                comment.getUpdatedAt());
    }
}
