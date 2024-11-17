package com.project.bee_rushtech.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.project.bee_rushtech.dtos.AuthorizeDTO;
import com.project.bee_rushtech.dtos.ChangePasswordDTO;
import com.project.bee_rushtech.models.User;
import com.project.bee_rushtech.responses.LoginResponse;
import com.project.bee_rushtech.responses.UserResponse;
import com.project.bee_rushtech.services.GoogleService;
import com.project.bee_rushtech.services.UserService;
import com.project.bee_rushtech.utils.SecurityUtil;
import com.project.bee_rushtech.utils.annotation.ApiMessage;
import com.project.bee_rushtech.utils.errors.InvalidException;

import jakarta.servlet.http.HttpServletResponse;
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
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final GoogleService googleUserInfoService;

    public UserController(UserService userService, PasswordEncoder passwordEncoder, SecurityUtil securityUtil,
            OAuth2AuthorizedClientService authorizedClientService, GoogleService googleUserInfoService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.securityUtil = securityUtil;
        this.authorizedClientService = authorizedClientService;
        this.googleUserInfoService = googleUserInfoService;

    }

    @PutMapping("/user/profile")
    public ResponseEntity<User> update(@Valid @CookieValue(name = "refresh_token", defaultValue = "") String token,
            @RequestBody User user)
            throws InvalidException {
        if (token.equals("")) {
            throw new InvalidException("You are not authorized");
        }
        Long userId = this.securityUtil.getUserFromToken(token).getId();
        User currentUser = this.userService.findById(userId);
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

    @GetMapping("/user/profile")
    @ApiMessage("Get information successfully")
    public ResponseEntity<UserResponse> getUserByEmail(
            @CookieValue(name = "refresh_token", defaultValue = "") String token)
            throws InvalidException {

        if (token.equals("")) {
            throw new InvalidException("You are not authorized");
        }
        Long userId = this.securityUtil.getUserFromToken(token).getId();
        User user = this.userService.getUserByRefreshTokenAndId(token, userId);
        if (user == null) {
            throw new InvalidException("You are not authorized");
        }
        UserResponse userResponse = new UserResponse(user.getId(), user.getFullName(), user.getEmail(),
                user.getPhoneNumber(), user.getAddress(), user.getRole());
        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }

    @GetMapping("/user/{id}")
    @ApiMessage("Get user successfully")
    public ResponseEntity<UserResponse> getUserById(
            @CookieValue(name = "refresh_token", defaultValue = "") String token,
            @PathVariable Long id)
            throws InvalidException {
        if (token.equals("")) {
            throw new InvalidException("You are not authorized");
        }
        String role = this.securityUtil.getUserFromToken(token).getRole();
        if (!role.equals("ADMIN")) {
            throw new InvalidException("You are not authorized");
        }
        User user = this.userService.findById(id);
        if (user == null) {
            throw new InvalidException("User not found");
        }
        UserResponse userResponse = new UserResponse(user.getId(), user.getFullName(), user.getEmail(),
                user.getPhoneNumber(), user.getAddress(), user.getRole());
        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }

    @GetMapping("/user")
    @ApiMessage("Get all users successfully")
    public ResponseEntity<List<UserResponse>> getAllUsers(
            @CookieValue(name = "refresh_token", defaultValue = "") String token)
            throws InvalidException {
        if (token.equals("")) {
            throw new InvalidException("You are not authorized");
        }
        String role = this.securityUtil.getUserFromToken(token).getRole();
        if (!role.equals("ADMIN")) {
            throw new InvalidException("You are not authorized");
        }
        List<User> users = this.userService.findAllUsers();
        if (users == null) {
            throw new InvalidException("Users not found");
        }
        List<UserResponse> userResponses = new ArrayList();
        for (User user : users) {
            UserResponse userResponse = new UserResponse(user.getId(), user.getFullName(), user.getEmail(),
                    user.getPhoneNumber(), user.getAddress(), user.getRole());
            userResponses.add(userResponse);
        }

        return ResponseEntity.status(HttpStatus.OK).body(userResponses);
    }

    @PutMapping("/user/change-password")
    @ApiMessage("Change password successfully")
    public ResponseEntity<Void> changePassword(
            @Valid @CookieValue(name = "refresh_token", defaultValue = "") String token,
            @RequestBody ChangePasswordDTO changePassword)
            throws InvalidException {
        if (token.equals("")) {
            throw new InvalidException("You are not authorized");
        }
        Long userId = this.securityUtil.getUserFromToken(token).getId();
        User currentUser = this.userService.findById(userId);
        if (currentUser == null) {
            throw new InvalidException("User not found");
        }
        if (!this.passwordEncoder.matches(changePassword.getOldPassword(), currentUser.getPassword())) {
            throw new InvalidException("Old password is incorrect");
        }
        if (!changePassword.getNewPassword().equals(changePassword.getConfirmPassword())) {
            throw new InvalidException("New password and confirm password are not the same");
        }
        String hashPassword = this.passwordEncoder.encode(changePassword.getNewPassword());
        currentUser.setPassword(hashPassword);
        this.userService.handleUpdateUser(currentUser);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PutMapping("/user/authorize")
    @ApiMessage("Authorize user successfully")
    public ResponseEntity<UserResponse> authorizeUser(
            @CookieValue(name = "refresh_token", defaultValue = "") String token,
            @RequestBody AuthorizeDTO authorizeDTO)
            throws InvalidException {
        if (token.equals("")) {
            throw new InvalidException("You are not authorized");
        }
        String role = this.securityUtil.getUserFromToken(token).getRole();
        if (!role.equals("ADMIN")) {
            throw new InvalidException("You are not authorized");
        }
        User currentUser = this.userService.getUserByEmail(authorizeDTO.getEmail());
        if (currentUser == null) {
            throw new InvalidException("User not found");
        }
        if (currentUser.getEmail().equals("beerushtech@gmail.com")) {
            throw new InvalidException("You can not change role of this user");
        }
        currentUser.setRole(authorizeDTO.getRole());
        User updatedUser = this.userService.handleUpdateUser(currentUser);
        UserResponse userResponse = new UserResponse(updatedUser.getId(), updatedUser.getFullName(),
                updatedUser.getEmail(),
                updatedUser.getPhoneNumber(), updatedUser.getAddress(), updatedUser.getRole());
        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }

}
