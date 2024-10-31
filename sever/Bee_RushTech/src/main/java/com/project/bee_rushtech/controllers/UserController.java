package com.project.bee_rushtech.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.project.bee_rushtech.models.User;
import com.project.bee_rushtech.services.UserService;
import com.project.bee_rushtech.utils.errors.InvalidException;
import jakarta.validation.Valid;

@RestController
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;

    }

    @PostMapping("/customer")
    public ResponseEntity<User> register(@Valid @RequestBody User user) throws InvalidException {
        if (this.userService.checkUserExists(user.getEmail())) {
            throw new InvalidException("Email is already taken");
        }
        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        User newUser = this.userService.handleCreateUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @PutMapping("/customer")
    public ResponseEntity<User> update(@Valid @RequestBody User user) throws InvalidException {
        User currentUser = this.userService.getUserByEmail(user.getEmail());
        if (currentUser == null) {
            throw new InvalidException("User not found");
        }
        if (user.getFirstName() == null || user.getLastName() == null || user.getPhoneNumber() == null
                || user.getAddress() == null) {
            throw new InvalidException("Please fill all fields");
        }
        currentUser.setFirstName(user.getFirstName());
        currentUser.setLastName(user.getLastName());
        currentUser.setPhoneNumber(user.getPhoneNumber());
        currentUser.setAddress(user.getAddress());
        User updatedUser = this.userService.handleUpdateUser(currentUser);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }

}
