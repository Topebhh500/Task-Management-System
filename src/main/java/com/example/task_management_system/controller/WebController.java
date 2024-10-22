package com.example.task_management_system.controller;

import com.example.task_management_system.model.Task;
import com.example.task_management_system.service.TaskService;
import com.example.task_management_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
public class WebController {

    private final TaskService taskService;
    private final UserService userService;

    @Autowired
    public WebController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    @GetMapping
    public String home(@AuthenticationPrincipal OAuth2User principal) {
        return principal != null ? "redirect:/tasks" : "login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/tasks")
    public String listTasks(Model model) {
        model.addAttribute("tasks", taskService.getAllTasks());
        return "task-list";
    }

    @GetMapping("/tasks/{id}")
    public String taskDetails(@PathVariable Long id, Model model) {
        taskService.getTaskById(id).ifPresent(task -> model.addAttribute("task", task));
        return "task-details";
    }

    @GetMapping("/tasks/create")
    public String createTaskForm(Model model) {
        model.addAttribute("task", new Task());
        model.addAttribute("users", userService.getAllUsers());
        return "task-form";
    }

    @PostMapping("/tasks")
    public String createOrUpdateTask(@ModelAttribute Task task) {
        taskService.createTask(task);
        return "redirect:/tasks";
    }

    @GetMapping("/tasks/{id}/edit")
    public String editTaskForm(@PathVariable Long id, Model model) {
        taskService.getTaskById(id).ifPresent(task -> model.addAttribute("task", task));
        model.addAttribute("users", userService.getAllUsers());
        return "task-form";
    }

    @GetMapping("/tasks/{id}/delete")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return "redirect:/tasks";
    }
}
