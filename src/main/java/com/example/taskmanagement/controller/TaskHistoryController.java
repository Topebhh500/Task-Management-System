package com.example.taskmanagement.controller;

import com.example.taskmanagement.model.TaskHistory;
import com.example.taskmanagement.service.TaskService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/task-history")
public class TaskHistoryController {
    private final TaskService taskService;

    public TaskHistoryController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public String viewTaskHistory(@RequestParam(required = false) String filter, 
                                Model model,
                                Authentication authentication) {
        String userEmail = authentication.getName();
        List<TaskHistory> history;

        if (filter != null) {
            switch (filter) {
                case "completed":
                    history = taskService.getUserTaskHistoryByType(userEmail, TaskHistory.ActionType.COMPLETED);
                    break;
                case "deleted":
                    history = taskService.getUserTaskHistoryByType(userEmail, TaskHistory.ActionType.DELETED);
                    break;
                default:
                    history = taskService.getUserTaskHistory(userEmail);
            }
        } else {
            history = taskService.getUserTaskHistory(userEmail);
        }

        model.addAttribute("taskHistory", history);
        model.addAttribute("currentFilter", filter);
        return "task-history/list";
    }
}