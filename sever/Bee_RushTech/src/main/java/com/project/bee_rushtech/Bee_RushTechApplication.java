package com.project.bee_rushtech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication

@SpringBootApplication(exclude = {
		org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class })
public class Bee_RushTechApplication {

	public static void main(String[] args) {
		SpringApplication.run(Bee_RushTechApplication.class, args);
	}

}
