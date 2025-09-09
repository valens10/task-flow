package valens.example.task_flow.tasks.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import valens.example.task_flow.tasks.entity.Task;
import valens.example.task_flow.tasks.entity.TaskComment;
import valens.example.task_flow.users.entity.User;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskCommentRepository extends JpaRepository<TaskComment, Long> {

    // Find comments for a specific task
    Page<TaskComment> findByTaskOrderByCreatedAtDesc(Task task, Pageable pageable);

    // Find comments by author
    Page<TaskComment> findByAuthorOrderByCreatedAtDesc(User author, Pageable pageable);

    // Find recent comments for a task
    @Query("SELECT tc FROM TaskComment tc WHERE tc.task.id = :taskId AND tc.deleted = false ORDER BY tc.createdAt DESC")
    List<TaskComment> findRecentCommentsForTask(@Param("taskId") UUID taskId, Pageable pageable);

    // Count comments for a task
    @Query("SELECT COUNT(tc) FROM TaskComment tc WHERE tc.task = :task AND tc.deleted = false")
    long countCommentsForTask(@Param("task") Task task);

    // Find comments containing specific text
    @Query("SELECT tc FROM TaskComment tc WHERE LOWER(tc.body) LIKE LOWER(CONCAT('%', :search, '%')) AND tc.deleted = false")
    Page<TaskComment> searchComments(@Param("search") String search, Pageable pageable);

    // Find comments by author for a specific task
    @Query("SELECT tc FROM TaskComment tc WHERE tc.task = :task AND tc.author = :author AND tc.deleted = false ORDER BY tc.createdAt DESC")
    List<TaskComment> findCommentsByAuthorForTask(@Param("task") Task task, @Param("author") User author);
}
