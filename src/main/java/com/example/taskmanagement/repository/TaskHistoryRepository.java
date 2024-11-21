package com.example.taskmanagement.repository;

import com.example.taskmanagement.model.TaskHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskHistoryRepository extends JpaRepository<TaskHistory, Long> {
    List<TaskHistory> findAllByActionByOrderByActionDateDesc(String actionBy);
    List<TaskHistory> findByActionByAndActionTypeOrderByActionDateDesc(String actionBy, TaskHistory.ActionType actionType);
    List<TaskHistory> findAllByOrderByActionDateDesc();
    List<TaskHistory> findByActionTypeOrderByActionDateDesc(TaskHistory.ActionType actionType);
}