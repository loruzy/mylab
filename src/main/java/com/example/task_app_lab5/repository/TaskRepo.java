package com.example.task_app_lab5.repository;

import com.example.task_app_lab5.model.Tasks;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepo extends JpaRepository<Tasks, Long> {

    // Add this method to handle filtering by user, status, and category
    Page<Tasks> findByUserIdAndStatusAndCategoryId(Long userId, String status, Long categoryId, Pageable pageable);

    Page<Tasks> findByUserId(Long userId, Pageable pageable);

    Page<Tasks> findByUserIdAndStatus(Long userId, String status, Pageable pageable);

    Page<Tasks> findByUserIdAndCategoryId(Long userId, Long categoryId, Pageable pageable);

    Page<Tasks> findByUserIdOrderByDueDate(Long userId, Pageable pageable);
}
