package com.subscription.core.controller;

import com.subscription.core.dto.UserUpsertDTO;
import com.subscription.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for User entity operations
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/upsert")
    public ResponseEntity<String> upsert(@RequestBody UserUpsertDTO userRequest) {
        try {
            String message = userService.upsertUser(userRequest);
            return ResponseEntity.ok(message);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error during User upsert operation: " + e.getMessage());
        }
    }
}
