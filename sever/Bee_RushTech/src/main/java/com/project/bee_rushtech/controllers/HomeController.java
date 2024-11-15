package com.project.bee_rushtech.controllers;

import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class HomeController {
    @GetMapping("/")
    public String getHomePage() {
        return "Welcome to the home page!";
    }

    @GetMapping("/get-user")
    public Principal getUser(Principal principal) {
        return principal;
    }

}
