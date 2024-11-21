package com.example.taskmanagement.service;

import com.example.taskmanagement.dto.TaskDTO;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.model.TaskHistory;
import com.example.taskmanagement.model.User;
import com.example.taskmanagement.repository.TaskHistoryRepository;
import com.example.taskmanagement.repository.TaskRepository;
import com.example.taskmanagement.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskHistoryRepository taskHistoryRepository;

    public TaskService(TaskRepository taskRepository, 
                      UserRepository userRepository,
                      TaskHistoryRepository taskHistoryRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.taskHistoryRepository = taskHistoryRepository;
    }

    public Task createTask(TaskDTO taskDTO, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Task task = new Task();
        updateTaskFromDTO(task, taskDTO);
        task.setUser(user);
        
        return taskRepository.save(task);
    }

    public Optional<Task> updateTask(Long taskId, TaskDTO taskDTO, String userEmail) {
        return taskRepository.findById(taskId)
            .filter(task -> task.getUser().getEmail().equals(userEmail))
            .map(task -> {
                Task.Status oldStatus = task.getStatus();
                updateTaskFromDTO(task, taskDTO);
                
                // Check if task is being completed through edit
                if (task.getStatus() == Task.Status.COMPLETED && oldStatus != Task.Status.COMPLETED) {
                    task.setCompletionDate(LocalDateTime.now());
                    // Create history record for completion via edit
                    TaskHistory history = createTaskHistory(task, TaskHistory.ActionType.COMPLETED, userEmail);
                    taskHistoryRepository.save(history);
                }
                
                return taskRepository.save(task);
            });
    }

    private void updateTaskFromDTO(Task task, TaskDTO dto) {
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPriority(dto.getPriority());
        task.setStatus(dto.getStatus());
        task.setDueDate(dto.getDueDate());
    }

    public List<Task> getUserTasks(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return taskRepository.findByUserIdOrderByDueDateAscCreatedAtDesc(user.getId());
    }

    public Optional<Task> getTask(Long taskId, String userEmail) {
        return taskRepository.findById(taskId)
            .filter(task -> task.getUser().getEmail().equals(userEmail));
    }

    @Transactional
    public boolean deleteTask(Long taskId, String userEmail) {
        return getTask(taskId, userEmail)
            .map(task -> {
                // Create history record before deletion
                TaskHistory history = createTaskHistory(task, TaskHistory.ActionType.DELETED, userEmail);
                taskHistoryRepository.save(history);
                
                // Delete the task
                taskRepository.delete(task);
                return true;
            })
            .orElse(false);
    }

    @Transactional
    public void startTask(Long taskId, String userEmail) {
        getTask(taskId, userEmail).ifPresent(task -> {
            if (task.getStatus() == Task.Status.TODO) {
                task.setStatus(Task.Status.IN_PROGRESS);
                taskRepository.save(task);
            }
        });
    }

    @Transactional
    public void completeTask(Long taskId, String userEmail) {
        getTask(taskId, userEmail).ifPresent(task -> {
            task.setStatus(Task.Status.COMPLETED);
            task.setCompletionDate(LocalDateTime.now());
            taskRepository.save(task);
            
            // Create history record
            TaskHistory history = createTaskHistory(task, TaskHistory.ActionType.COMPLETED, userEmail);
            taskHistoryRepository.save(history);
        });
    }

    private TaskHistory createTaskHistory(Task task, TaskHistory.ActionType actionType, String userEmail) {
        TaskHistory history = new TaskHistory();
        history.setOriginalTaskId(task.getId());
        history.setTitle(task.getTitle());
        history.setDescription(task.getDescription());
        history.setDueDate(task.getDueDate());
        history.setPriority(task.getPriority());
        history.setStatus(task.getStatus());
        history.setActionType(actionType);
        history.setActionDate(LocalDateTime.now());
        history.setCompletionDate(task.getCompletionDate());
        history.setActionBy(userEmail);
        
        return history;
    }

    public Map<String, List<Task>> getUserTasksByStatus(String userEmail) {
        List<Task> tasks = getUserTasks(userEmail);
        Map<Task.Status, List<Task>> groupedTasks = tasks.stream()
            .collect(Collectors.groupingBy(Task::getStatus));
        
        // Initialize map with empty lists for all statuses
        Map<String, List<Task>> result = new HashMap<>();
        result.put("TODO", groupedTasks.getOrDefault(Task.Status.TODO, new ArrayList<>()));
        result.put("IN_PROGRESS", groupedTasks.getOrDefault(Task.Status.IN_PROGRESS, new ArrayList<>()));
        result.put("COMPLETED", groupedTasks.getOrDefault(Task.Status.COMPLETED, new ArrayList<>()));
        
        return result;
    }

    public Map<String, Long> getTaskStatistics(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Map<String, Long> statistics = new HashMap<>();
        statistics.put("todo", taskRepository.countByUserIdAndStatus(user.getId(), Task.Status.TODO));
        statistics.put("in_progress", taskRepository.countByUserIdAndStatus(user.getId(), Task.Status.IN_PROGRESS));
        statistics.put("completed", taskRepository.countByUserIdAndStatus(user.getId(), Task.Status.COMPLETED));
        
        return statistics;
    }

    public List<Task> searchTasks(String userEmail, String searchQuery, Task.Priority priority, Set<String> tags) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return taskRepository.searchTasks(user.getId(), searchQuery, priority, tags);
    }

    public Set<String> getUserTags(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return taskRepository.findAllTagsByUserId(user.getId());
    }

    @Transactional
    public void updateTaskStatus(Long taskId, Task.Status newStatus, String userEmail) {
        getTask(taskId, userEmail).ifPresent(task -> {
            Task.Status oldStatus = task.getStatus();
            task.setStatus(newStatus);
            
            // If task is being completed, add to history and set completion date
            if (newStatus == Task.Status.COMPLETED && oldStatus != Task.Status.COMPLETED) {
                task.setCompletionDate(LocalDateTime.now());
                TaskHistory history = createTaskHistory(task, TaskHistory.ActionType.COMPLETED, userEmail);
                taskHistoryRepository.save(history);
            }
            
            taskRepository.save(task);
        });
    }

    // Task History related methods
    public List<TaskHistory> getUserTaskHistory(String userEmail) {
        return taskHistoryRepository.findAllByActionByOrderByActionDateDesc(userEmail);
    }

    public List<TaskHistory> getUserTaskHistoryByType(String userEmail, TaskHistory.ActionType actionType) {
        return taskHistoryRepository.findByActionByAndActionTypeOrderByActionDateDesc(userEmail, actionType);
    }
}