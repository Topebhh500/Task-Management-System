package com.taskmanagement.controller;

import com.taskmanagement.model.User;
import com.taskmanagement.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, 
                                BindingResult bindingResult, 
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            userService.registerNewUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful. Please login.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
}