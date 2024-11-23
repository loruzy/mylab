package com.example.task_app_lab5.controller;

import com.example.task_app_lab5.model.User_table;
import com.example.task_app_lab5.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {
    @Autowired
    private UserRepo userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(String username, String password, Model model) {
        if (userRepository.findByUsername(username) != null) {
            model.addAttribute("error", "This username is already exist. Pick another one.");
            return "register";
        }

        String role = "USER";
        if ("admin".equals(username) && "admin".equals(password)) {
            role = "ADMIN";
        }

        User_table user = new User_table(username, passwordEncoder.encode(password), role);
        userRepository.save(user);

        return "redirect:/login";
    }
}
