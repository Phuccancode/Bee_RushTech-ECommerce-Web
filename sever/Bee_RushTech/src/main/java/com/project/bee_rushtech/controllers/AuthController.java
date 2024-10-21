package com.project.bee_rushtech.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.project.bee_rushtech.dtos.LoginDTO;
import com.project.bee_rushtech.dtos.ResLoginDTO;
import com.project.bee_rushtech.models.User;
import com.project.bee_rushtech.services.AuthService;
import com.project.bee_rushtech.utils.SecurityUtil;
import com.project.bee_rushtech.utils.errors.InvalidException;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class AuthController {

    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;

    public AuthController(AuthService authService, PasswordEncoder passwordEncoder,
            AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil) {
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<User> register(@Valid @RequestBody User user) throws InvalidException {
        if (this.authService.checkUserExists(user.getEmail())) {
            throw new InvalidException("Email is already taken");
        }
        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        User newUser = this.authService.handleCreateUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody LoginDTO loginDTO) {

        // Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword());

        // xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // create a token
        String access_token = this.securityUtil.createAccessToken(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication); // set authentication vào SecurityContext
        ResLoginDTO resLoginDTO = new ResLoginDTO();
        User userDB = this.authService.loadUserByUsername(loginDTO.getUsername());
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(userDB.getId(), userDB.getEmail(),
                userDB.getFirstName());
        resLoginDTO.setUser(userLogin);
        resLoginDTO.setAccess_token(access_token);
        String refresh_token = this.securityUtil.createRefreshToken(loginDTO.getUsername(), resLoginDTO);
        return ResponseEntity.ok().body(resLoginDTO);
    }

    @GetMapping("/")
    public String getHomePage() {
        return "Welcome to Bee Rushtech by GET";
    }

    @PostMapping("/")
    public String postHomePage() {
        return "Welcome to Bee Rushtech by POST";
    }

}
