package com.project.bee_rushtech.models;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String email;
    private String password;
    private String address;
    private String phoneNumber;

    private Instant createdAt;
    private Instant updatedAt;

    private Boolean isActive;

    private Date dateOfBirth;

    private String facebookId;
    private String googleId;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String refreshToken;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String passwordResetToken;

    @ManyToOne
    @JoinColumn(name = "roleId")
    private Role role;

}
