package com.project.bee_rushtech.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDTO {
    @NotBlank(message = "email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

}
