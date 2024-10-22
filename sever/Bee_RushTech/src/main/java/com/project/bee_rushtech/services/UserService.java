package com.project.bee_rushtech.services;

import org.springframework.stereotype.Service;

import com.project.bee_rushtech.models.User;
import com.project.bee_rushtech.repositories.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User handleCreateUser(User user) {
        return this.userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public boolean checkUserExists(String email) {
        return this.userRepository.existsByEmail(email);
    }

}
