package com.example.taskmanagement.repository;

import com.example.taskmanagement.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Set;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserIdOrderByDueDateAscCreatedAtDesc(Long userId);
    
    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.status = :status ORDER BY t.dueDate ASC, t.createdAt DESC")
    List<Task> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Task.Status status);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.user.id = :userId AND t.status = :status")
    long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Task.Status status);

    @Query("SELECT DISTINCT t FROM Task t LEFT JOIN t.tags tags " +
           "WHERE t.user.id = :userId " +
           "AND (:searchQuery IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :searchQuery, '%')) " +
           "    OR LOWER(t.description) LIKE LOWER(CONCAT('%', :searchQuery, '%'))) " +
           "AND (:priority IS NULL OR t.priority = :priority) " +
           "AND (COALESCE(:tags, NULL) IS NULL OR tags IN :tags) " +
           "ORDER BY t.dueDate ASC, t.createdAt DESC")
    List<Task> searchTasks(
        @Param("userId") Long userId,
        @Param("searchQuery") String searchQuery,
        @Param("priority") Task.Priority priority,
        @Param("tags") Set<String> tags
    );

    @Query("SELECT DISTINCT tags FROM Task t JOIN t.tags tags WHERE t.user.id = :userId")
    Set<String> findAllTagsByUserId(@Param("userId") Long userId);
}