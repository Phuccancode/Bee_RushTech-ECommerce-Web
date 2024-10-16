package com.project.bee_rushtech.controller;

import org.springframework.web.bind.annotation.RestController;
import com.project.bee_rushtech.service.UserService;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

}
