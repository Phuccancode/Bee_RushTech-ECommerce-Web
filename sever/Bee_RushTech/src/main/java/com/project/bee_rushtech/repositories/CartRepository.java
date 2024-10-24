package com.project.bee_rushtech.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.bee_rushtech.models.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
