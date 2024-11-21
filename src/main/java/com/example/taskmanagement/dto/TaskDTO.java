package com.example.taskmanagement.dto;

import com.example.taskmanagement.model.Task.Priority;
import com.example.taskmanagement.model.Task.Status;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private Priority priority;
    private Status status;
    private Set<String> tags;
    private String searchQuery;
    private Priority priorityFilter;
    private Set<String> selectedTags;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dueDate;
}