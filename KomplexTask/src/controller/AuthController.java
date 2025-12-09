package com.example.springbootxml.controller;

import com.example.springbootxml.model.User;
import com.example.springbootxml.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "auth/login";  // Теперь файл существует
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password) {
        User user = userService.getUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            // Временно упрощенная логика
            if (user.getUsername().equals("librarian")) {
                return "redirect:/librarian/dashboard";
            } else {
                return "redirect:/reader/dashboard";
            }
        }
        return "redirect:/login?error=true";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String fullName,
                           Model model) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setFullName(fullName);

        userService.registerUser(user);
        return "redirect:/login";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "redirect:/reader/dashboard";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }
}
