package com.example.taskmanagement.controller;

import com.example.taskmanagement.dto.TaskDTO;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.security.CustomUserDetails;
import com.example.taskmanagement.service.TaskService;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    private String getUserEmail(Authentication authentication) {
        if (authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            return oauth2User.getAttribute("email");
        } else {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getUsername(); // Email is used as username
        }
    }

    @GetMapping
    public String listTasks(Authentication authentication, Model model) {
        String email = getUserEmail(authentication);
        model.addAttribute("tasks", taskService.getUserTasksByStatus(email));
        model.addAttribute("statistics", taskService.getTaskStatistics(email));
        model.addAttribute("userEmail", email);
        return "tasks/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("task", new TaskDTO());
        model.addAttribute("priorities", Task.Priority.values());
        model.addAttribute("statuses", Task.Status.values());
        return "tasks/form";
    }

    @PostMapping
    public String createTask(
            Authentication authentication,
            @ModelAttribute TaskDTO taskDTO,
            RedirectAttributes attributes) {
        String email = getUserEmail(authentication);
        taskService.createTask(taskDTO, email);
        attributes.addFlashAttribute("message", "Task created successfully!");
        return "redirect:/tasks";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(
            Authentication authentication,
            @PathVariable Long id,
            Model model) {
        String email = getUserEmail(authentication);
        taskService.getTask(id, email).ifPresent(task -> {
            TaskDTO dto = new TaskDTO();
            dto.setId(task.getId());
            dto.setTitle(task.getTitle());
            dto.setDescription(task.getDescription());
            dto.setPriority(task.getPriority());
            dto.setStatus(task.getStatus());
            dto.setDueDate(task.getDueDate());
            
            model.addAttribute("task", dto);
            model.addAttribute("priorities", Task.Priority.values());
            model.addAttribute("statuses", Task.Status.values());
        });
        return "tasks/form";
    }

    @PostMapping("/{id}")
    public String updateTask(
            Authentication authentication,
            @PathVariable Long id,
            @ModelAttribute TaskDTO taskDTO,
            RedirectAttributes attributes) {
        String email = getUserEmail(authentication);
        if (taskService.updateTask(id, taskDTO, email).isPresent()) {
            attributes.addFlashAttribute("message", "Task updated successfully!");
        }
        return "redirect:/tasks";
    }

    @GetMapping("/search")
    public String searchTasks(
            @AuthenticationPrincipal OAuth2User principal,
            @ModelAttribute TaskDTO searchDTO,
            Model model) {
        String email = principal.getAttribute("email");
        model.addAttribute("tasks", taskService.searchTasks(
            email,
            searchDTO.getSearchQuery(),
            searchDTO.getPriorityFilter(),
            searchDTO.getSelectedTags()
        ));
        model.addAttribute("availableTags", taskService.getUserTags(email));
        model.addAttribute("priorities", Task.Priority.values());
        return "tasks/list :: taskList";
    }

    @PutMapping("/{id}/status")
    @ResponseBody
    public ResponseEntity<Void> updateTaskStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload,
            Authentication authentication) {
        String email = getUserEmail(authentication);
        Task.Status newStatus = Task.Status.valueOf(payload.get("status"));
        taskService.updateTaskStatus(id, newStatus, email);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, Authentication authentication) {
        if (taskService.deleteTask(id, authentication.getName())) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/complete")
    @ResponseBody
    public ResponseEntity<Void> completeTask(@PathVariable Long id, Authentication authentication) {
        taskService.completeTask(id, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/start")
    @ResponseBody
    public ResponseEntity<Void> startTask(@PathVariable Long id, Authentication authentication) {
        taskService.startTask(id, authentication.getName());
        return ResponseEntity.ok().build();
    }
}