package com.project.bee_rushtech.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.bee_rushtech.models.CartItem;
import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findAllById(Long id); // Phương thức để tìm các mục giỏ hàng theo userId
}
