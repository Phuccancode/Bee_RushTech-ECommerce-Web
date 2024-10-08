package com.project.beerushtech.repository;

import org.springframework.stereotype.Repository;

import com.project.beerushtech.domain.User;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
