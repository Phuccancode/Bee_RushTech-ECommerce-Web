package com.project.bee_rushtech.controllers;

import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.bee_rushtech.models.User;
import com.project.bee_rushtech.services.UserService;
import com.project.bee_rushtech.utils.SecurityUtil;
import com.project.bee_rushtech.utils.annotation.ApiMessage;
import com.project.bee_rushtech.utils.errors.InvalidException;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("${api.prefix}")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtil securityUtil;

    public UserController(UserService userService, PasswordEncoder passwordEncoder, SecurityUtil securityUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.securityUtil = securityUtil;

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
        if (user.getFullName() == null || user.getPhoneNumber() == null
                || user.getAddress() == null) {
            throw new InvalidException("Please fill all fields");
        }
        currentUser.setFullName(user.getFullName());
        currentUser.setPhoneNumber(user.getPhoneNumber());
        currentUser.setAddress(user.getAddress());
        User updatedUser = this.userService.handleUpdateUser(currentUser);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }

    @GetMapping("/customer")
    @ApiMessage("Get information successfully")
    public ResponseEntity<User> getUserByEmail(@CookieValue(name = "refresh_token") String token)
            throws InvalidException {
        System.out.println(token);
        Jwt tokenDecoded = this.securityUtil.checkValidRefreshToken(token);
        String email = tokenDecoded.getSubject();
        User user = this.userService.getUserByRefreshTokenAndEmail(token, email);
        if (user == null) {
            throw new InvalidException("You are not authorized");
        }
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping("/customer/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User user = this.userService.findById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping("/admin/customer")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = this.userService.findAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

}
