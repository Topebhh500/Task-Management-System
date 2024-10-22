package com.example.task_management_system.service;

import com.example.task_management_system.model.Task;
import com.example.task_management_system.model.TaskStatus;

import java.util.List;
import java.util.Optional;

public interface TaskService {
    Task createTask(Task task);
    Optional<Task> getTaskById(Long id);
    List<Task> getAllTasks();
    List<Task> getTasksByUserId(Long userId);
    List<Task> getTasksByUserIdAndStatus(Long userId, TaskStatus status);
    Task updateTask(Task task);
    void deleteTask(Long id);
}