package com.project.bee_rushtech.repository;

import org.springframework.stereotype.Repository;

import com.project.bee_rushtech.domain.User;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
