package com.project.bee_rushtech.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.project.bee_rushtech.models.Email;
import com.project.bee_rushtech.services.EmailService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class EmailController {
    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/email")
    public ResponseEntity<Void> sendEmail(@RequestBody Email email) {
        this.emailService.sendEmail(email);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
