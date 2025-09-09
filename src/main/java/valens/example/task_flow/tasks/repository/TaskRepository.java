package valens.example.task_flow.tasks.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import valens.example.task_flow.tasks.entity.Task;
import valens.example.task_flow.tasks.entity.TaskStatus;
import valens.example.task_flow.tasks.entity.Priority;
import valens.example.task_flow.users.entity.User;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    // Find tasks by assignee
    Page<Task> findByAssignee(User assignee, Pageable pageable);

    // Find tasks by reporter
    Page<Task> findByReporter(User reporter, Pageable pageable);

    // Find tasks by status
    Page<Task> findByStatus(TaskStatus status, Pageable pageable);

    // Find tasks by priority
    Page<Task> findByPriority(Priority priority, Pageable pageable);

    // Find tasks due before a specific date
    @Query("SELECT t FROM Task t WHERE t.dueDate < :date AND t.deleted = false")
    Page<Task> findOverdueTasks(@Param("date") LocalDate date, Pageable pageable);

    // Find tasks due today
    @Query("SELECT t FROM Task t WHERE t.dueDate = :date AND t.deleted = false")
    Page<Task> findTasksDueToday(@Param("date") LocalDate date, Pageable pageable);

    // Find tasks assigned to user or created by user
    @Query("SELECT t FROM Task t WHERE (t.assignee = :user OR t.reporter = :user) AND t.deleted = false")
    Page<Task> findTasksForUser(@Param("user") User user, Pageable pageable);

    // Search tasks by title or description
    @Query("SELECT t FROM Task t WHERE (LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND t.deleted = false")
    Page<Task> searchTasks(@Param("search") String search, Pageable pageable);

    // Find tasks with multiple filters
    @Query("SELECT t FROM Task t WHERE " +
            "(:assignee IS NULL OR t.assignee = :assignee) AND " +
            "(:reporter IS NULL OR t.reporter = :reporter) AND " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:priority IS NULL OR t.priority = :priority) AND " +
            "(:dueDateFrom IS NULL OR t.dueDate >= :dueDateFrom) AND " +
            "(:dueDateTo IS NULL OR t.dueDate <= :dueDateTo) AND " +
            "t.deleted = false")
    Page<Task> findTasksWithFilters(
            @Param("assignee") User assignee,
            @Param("reporter") User reporter,
            @Param("status") TaskStatus status,
            @Param("priority") Priority priority,
            @Param("dueDateFrom") LocalDate dueDateFrom,
            @Param("dueDateTo") LocalDate dueDateTo,
            Pageable pageable);

    // Count tasks by status for a user
    @Query("SELECT t.status, COUNT(t) FROM Task t WHERE (t.assignee = :user OR t.reporter = :user) AND t.deleted = false GROUP BY t.status")
    List<Object[]> countTasksByStatusForUser(@Param("user") User user);

    // Find tasks created in the last N days
    @Query("SELECT t FROM Task t WHERE t.createdAt >= :since AND t.deleted = false")
    Page<Task> findRecentTasks(@Param("since") java.time.Instant since, Pageable pageable);
}
