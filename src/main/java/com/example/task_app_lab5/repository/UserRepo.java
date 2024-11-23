package com.example.task_app_lab5.repository;

import com.example.task_app_lab5.model.User_table;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User_table, Long>{
    User_table findByUsername(String username);
}
