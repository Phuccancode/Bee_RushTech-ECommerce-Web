package com.project.bee_rushtech.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.project.bee_rushtech.services.UserService;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

}
