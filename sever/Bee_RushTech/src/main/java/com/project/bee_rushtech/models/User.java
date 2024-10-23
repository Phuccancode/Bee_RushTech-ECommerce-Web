package com.project.bee_rushtech.models;

import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

import org.springframework.security.access.method.P;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private String address;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String refreshToken;

}
