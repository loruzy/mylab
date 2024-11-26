package com.example.task_app_lab5.controller;

import com.example.task_app_lab5.model.Role;
import com.example.task_app_lab5.model.User_table;
import com.example.task_app_lab5.repository.UserRepo;
import com.example.task_app_lab5.repository.RoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@Controller
public class AuthController {

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private RoleRepo roleRepository;

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
            model.addAttribute("error", "This username already exists. Pick another one.");
            return "register";
        }

        // Default role is USER
        Set<Role> roles = new HashSet<>();
        Role role = roleRepository.findByName("USER").orElseThrow(() -> new IllegalArgumentException("Role not found"));
        roles.add(role);

        // Hardcoded check for admin credentials
        if ("admin".equals(username) && "admin".equals(password)) {
            role = roleRepository.findByName("ADMIN").orElseThrow(() -> new IllegalArgumentException("Role not found"));
            roles.clear();  // Clear USER role
            roles.add(role);
        }

        // Save user with encoded password and roles
        User_table user = new User_table(username, passwordEncoder.encode(password), roles);
        userRepository.save(user);

        return "redirect:/login";
    }
}

