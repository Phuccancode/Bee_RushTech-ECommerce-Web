package com.project.bee_rushtech.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.bee_rushtech.domain.User;

@Repository
public interface AuthRepository extends JpaRepository<User, Long> {
    User save(User user);

    User findByEmail(String email);
}
