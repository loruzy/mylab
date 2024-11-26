package com.example.task_app_lab5;

import com.example.task_app_lab5.model.User_table;
import com.example.task_app_lab5.repository.UserRepo;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User_table user = userRepo.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        // Assuming the user has at least one role assigned, we get the first role for simplicity.
        // You can handle multiple roles as needed (e.g., multiple authorities).
        String role = user.getRoles().stream()
                .findFirst() // Get the first role (you can modify it if needed to handle multiple roles)
                .map(roleObj -> "ROLE_" + roleObj.getName())  // Prefix "ROLE_" is required by Spring Security
                .orElse("ROLE_USER");  // Default to a standard role if no roles are found

        return new User(user.getUsername(), user.getPassword(), Collections.singletonList(new SimpleGrantedAuthority(role)));
    }
}
