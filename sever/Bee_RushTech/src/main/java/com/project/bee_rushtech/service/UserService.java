package com.project.bee_rushtech.service;

import org.springframework.stereotype.Service;
import com.project.bee_rushtech.repository.UserRepository;
import com.project.bee_rushtech.domain.User;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User handleUpdateUser(User user) {
        return this.userRepository.save(user);
    }
}
