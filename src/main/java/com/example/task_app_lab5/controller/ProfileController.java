package com.example.task_app_lab5.controller;

import com.example.task_app_lab5.model.User_table;
import com.example.task_app_lab5.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Controller
public class ProfileController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/profile")
    public String viewProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User_table user = userRepo.findByUsername(userDetails.getUsername());
        if (user == null) {
            throw new IllegalStateException("User not found");
        }
        model.addAttribute("user", user);
        return "profile"; // HTML template to display profile
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute User_table updatedUser, @AuthenticationPrincipal UserDetails userDetails) {
        User_table user = userRepo.findByUsername(userDetails.getUsername());
        if (user == null) {
            throw new IllegalStateException("User not found");
        }

        // Update profile information
        user.setEmail(updatedUser.getEmail());
        user.setProfileImageUrl(updatedUser.getProfileImageUrl()); // Update photo URL
        userRepo.save(user);
        return "redirect:/profile";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(@RequestParam String oldPassword, @RequestParam String newPassword, @AuthenticationPrincipal UserDetails userDetails) {
        User_table user = userRepo.findByUsername(userDetails.getUsername());
        if (user == null) {
            throw new IllegalStateException("User not found");
        }

        // Check if the old password matches the encoded password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        // Save the new password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
        return "redirect:/profile";
    }

    @PostMapping("/profile/upload-photo")
    public String uploadProfilePhoto(@RequestParam("photo") MultipartFile photo, @AuthenticationPrincipal UserDetails userDetails) {
        User_table user = userRepo.findByUsername(userDetails.getUsername());
        if (user == null) {
            throw new IllegalStateException("User not found");
        }

        // Logic to save photo
        String photoUrl = savePhoto(photo);
        user.setProfileImageUrl(photoUrl);
        userRepo.save(user);

        return "redirect:/profile";
    }

    private String savePhoto(MultipartFile photo) {
        // Logic to save the photo to a folder and get URL
        String fileName = "uploads/" + photo.getOriginalFilename();
        try {
            photo.transferTo(new File(fileName));  // Save photo to disk
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName; // Return the path to the photo
    }
}
