package com.project.bee_rushtech.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String getHelloWorld() {
        return "Đặt tên file chạy k được luôn !!!!";
    }
}
