package com.project.bee_rushtech.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.project.bee_rushtech.dtos.LoginDTO;
import com.project.bee_rushtech.models.User;
import com.project.bee_rushtech.services.AuthService;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        User newUser = this.authService.handleCreateUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<String> login(@RequestBody LoginDTO user) {
        User newUser = this.authService.loadUserByUsername(user.getEmail());
        return newUser.getPassword().equals(user.getPassword()) ? ResponseEntity.ok("Login successful")
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
    }

}
