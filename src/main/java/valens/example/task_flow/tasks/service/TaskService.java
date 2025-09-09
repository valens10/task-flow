package valens.example.task_flow.tasks.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import valens.example.task_flow.tasks.dto.*;
import valens.example.task_flow.tasks.entity.Task;
import valens.example.task_flow.tasks.entity.TaskStatus;
import valens.example.task_flow.tasks.entity.Priority;
import valens.example.task_flow.tasks.repository.TaskRepository;
import valens.example.task_flow.users.entity.User;
import valens.example.task_flow.users.repository.UserRepository;

import java.time.LocalDate;
import java.util.UUID;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public TaskResponse createTask(CreateTaskRequest request, User reporter) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setReporter(reporter);
        task.setDueDate(request.getDueDate());
        task.setPriority(request.getPriority());
        task.setCreatedAt(java.time.Instant.now());
        task.setUpdatedAt(java.time.Instant.now());

        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new IllegalArgumentException("Assignee not found"));
            task.setAssignee(assignee);
        }

        Task savedTask = taskRepository.save(task);
        return mapToTaskResponse(savedTask);
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(UUID id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        return mapToTaskResponse(task);
    }

    public TaskResponse updateTask(UUID id, UpdateTaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new IllegalArgumentException("Assignee not found"));
            task.setAssignee(assignee);
        }

        task.setUpdatedAt(java.time.Instant.now());
        Task updatedTask = taskRepository.save(task);
        return mapToTaskResponse(updatedTask);
    }

    public void deleteTask(UUID id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        task.setDeleted(true);
        taskRepository.save(task);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> getAllTasks(Pageable pageable) {
        return taskRepository.findAll(pageable)
                .map(this::mapToTaskResponse);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasksForUser(User user, Pageable pageable) {
        return taskRepository.findTasksForUser(user, pageable)
                .map(this::mapToTaskResponse);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasksByAssignee(User assignee, Pageable pageable) {
        return taskRepository.findByAssignee(assignee, pageable)
                .map(this::mapToTaskResponse);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasksByReporter(User reporter, Pageable pageable) {
        return taskRepository.findByReporter(reporter, pageable)
                .map(this::mapToTaskResponse);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasksByStatus(TaskStatus status, Pageable pageable) {
        return taskRepository.findByStatus(status, pageable)
                .map(this::mapToTaskResponse);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasksByPriority(Priority priority, Pageable pageable) {
        return taskRepository.findByPriority(priority, pageable)
                .map(this::mapToTaskResponse);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> getOverdueTasks(Pageable pageable) {
        return taskRepository.findOverdueTasks(LocalDate.now(), pageable)
                .map(this::mapToTaskResponse);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasksDueToday(Pageable pageable) {
        return taskRepository.findTasksDueToday(LocalDate.now(), pageable)
                .map(this::mapToTaskResponse);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> searchTasks(String search, Pageable pageable) {
        return taskRepository.searchTasks(search, pageable)
                .map(this::mapToTaskResponse);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasksWithFilters(User assignee, User reporter, TaskStatus status,
            Priority priority, LocalDate dueDateFrom,
            LocalDate dueDateTo, Pageable pageable) {
        return taskRepository.findTasksWithFilters(assignee, reporter, status, priority,
                dueDateFrom, dueDateTo, pageable)
                .map(this::mapToTaskResponse);
    }

    private TaskResponse mapToTaskResponse(Task task) {
        TaskResponse.UserInfo assigneeInfo = null;
        if (task.getAssignee() != null) {
            assigneeInfo = new TaskResponse.UserInfo(
                    task.getAssignee().getId(),
                    task.getAssignee().getEmail(),
                    task.getAssignee().getFullName());
        }

        TaskResponse.UserInfo reporterInfo = new TaskResponse.UserInfo(
                task.getReporter().getId(),
                task.getReporter().getEmail(),
                task.getReporter().getFullName());

        long commentCount = task.getComments().size();

        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                assigneeInfo,
                reporterInfo,
                task.getDueDate(),
                task.getPriority(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                commentCount);
    }
}
