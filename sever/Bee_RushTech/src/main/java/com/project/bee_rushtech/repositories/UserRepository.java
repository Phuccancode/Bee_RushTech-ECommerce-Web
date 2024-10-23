package com.project.bee_rushtech.repositories;

import org.springframework.stereotype.Repository;

import com.project.bee_rushtech.models.User;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User save(User user);

    User findByEmail(String email);

    boolean existsByEmail(String email);

    User findByRefreshTokenAndEmail(String refreshToken, String email);
}
