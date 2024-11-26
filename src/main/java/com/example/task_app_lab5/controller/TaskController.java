package com.example.task_app_lab5.controller;

import com.example.task_app_lab5.model.Category;
import com.example.task_app_lab5.model.Tasks;
import com.example.task_app_lab5.model.User_table;
import com.example.task_app_lab5.repository.CategoryRepo;
import com.example.task_app_lab5.repository.TaskRepo;
import com.example.task_app_lab5.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class TaskController {

    @Autowired
    private TaskRepo taskRepository;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private CategoryRepo categoryRepository;

    @GetMapping("/tasks")
    public String viewTasks(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,  // Default to page 0
            @RequestParam(defaultValue = "") String status,
            @RequestParam(defaultValue = "") Long categoryId,
            Model model) {

        User_table user = userRepo.findByUsername(userDetails.getUsername());
        if (user == null) throw new IllegalStateException("User not found");

        Pageable pageable = PageRequest.of(page, 10);  // Pagination with 10 tasks per page

        Page<Tasks> taskPage;

        // Apply filters if status or categoryId is provided
        if (!status.isEmpty() && categoryId != null) {
            taskPage = taskRepository.findByUserIdAndStatusAndCategoryId(user.getId(), status, categoryId, pageable);
        } else if (!status.isEmpty()) {
            taskPage = taskRepository.findByUserIdAndStatus(user.getId(), status, pageable);
        } else if (categoryId != null) {
            taskPage = taskRepository.findByUserIdAndCategoryId(user.getId(), categoryId, pageable);
        } else {
            taskPage = taskRepository.findByUserId(user.getId(), pageable);  // Fetch tasks without filters
        }

        model.addAttribute("tasks", taskPage.getContent());
        model.addAttribute("totalPages", taskPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("status", status);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("categories", categoryRepository.findAll());

        return "tasks";  // View with tasks
    }

    // Methods for filtering and pagination, using Page for tasks
    @PostMapping("/tasks/filter/status")
    public String filterTaskByStatus(
            @RequestParam("status") String status,
            @RequestParam(defaultValue = "0") int page,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        User_table user = userRepo.findByUsername(userDetails.getUsername());
        if (user == null) throw new IllegalStateException("User not found");

        Pageable pageable = PageRequest.of(page, 10);
        Page<Tasks> taskPage = taskRepository.findByUserIdAndStatus(user.getId(), status, pageable);

        model.addAttribute("tasks", taskPage.getContent());
        model.addAttribute("totalPages", taskPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("status", status);
        return "tasks";
    }

    @PostMapping("/tasks/filter/category")
    public String filterTaskByCategory(
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        User_table user = userRepo.findByUsername(userDetails.getUsername());
        if (user == null) throw new IllegalStateException("User not found");

        Pageable pageable = PageRequest.of(page, 10);
        Page<Tasks> taskPage = taskRepository.findByUserIdAndCategoryId(user.getId(), categoryId, pageable);

        model.addAttribute("tasks", taskPage.getContent());
        model.addAttribute("totalPages", taskPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("categoryId", categoryId);
        return "tasks";
    }

    @PostMapping("/tasks/sort/dueDate")
    public String sortTasksByDueDate(
            @RequestParam(defaultValue = "0") int page,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        User_table user = userRepo.findByUsername(userDetails.getUsername());
        if (user == null) throw new IllegalStateException("User not found");

        Pageable pageable = PageRequest.of(page, 10);
        Page<Tasks> taskPage = taskRepository.findByUserIdOrderByDueDate(user.getId(), pageable);

        model.addAttribute("tasks", taskPage.getContent());
        model.addAttribute("totalPages", taskPage.getTotalPages());
        model.addAttribute("currentPage", page);
        return "tasks";
    }
}
