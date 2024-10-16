package com.project.bee_rushtech.service;

import org.springframework.stereotype.Service;

import com.project.bee_rushtech.domain.User;
import com.project.bee_rushtech.repository.AuthRepository;

@Service
public class AuthService {
    private final AuthRepository authRepository;

    public AuthService(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public User handleCreateUser(User user) {
        return this.authRepository.save(user);
    }

    public User loadUserByUsername(String email) {
        return this.authRepository.findByEmail(email);
    }
}
