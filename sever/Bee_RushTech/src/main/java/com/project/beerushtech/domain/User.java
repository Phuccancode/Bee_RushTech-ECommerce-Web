package com.project.beerushtech.domain;

import lombok.Getter;
import lombok.Setter;
import java.time.Instant;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String email;
    private String password;

    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;
}
