package com.example.taskmanagement.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.taskmanagement.security.CustomUserDetails;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String showDashboard(Authentication authentication, Model model) {
        String name;
        String email;
        String picture = null;

        if (authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            name = oauth2User.getAttribute("name");
            email = oauth2User.getAttribute("email");
            picture = oauth2User.getAttribute("picture");
        } else {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            name = userDetails.getUser().getFullName();
            email = userDetails.getUsername(); 
        }

        model.addAttribute("userName", name);
        model.addAttribute("userEmail", email);
        if (picture != null) {
            model.addAttribute("userPicture", picture);
        }

        return "dashboard";
    }
}