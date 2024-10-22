package com.project.bee_rushtech.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.project.bee_rushtech.dtos.LoginDTO;
import com.project.bee_rushtech.dtos.ResLoginDTO;
import com.project.bee_rushtech.models.User;
import com.project.bee_rushtech.services.UserService;
import com.project.bee_rushtech.utils.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;

    public AuthController(UserService userService,
            AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil) {
        this.userService = userService;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
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
        User userDB = this.userService.getUserByEmail(loginDTO.getUsername());
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(userDB.getId(), userDB.getEmail(),
                userDB.getFirstName());
        resLoginDTO.setUser(userLogin);
        resLoginDTO.setAccess_token(access_token);
        // create refresh token
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
