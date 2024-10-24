package com.project.bee_rushtech.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.bee_rushtech.models.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
