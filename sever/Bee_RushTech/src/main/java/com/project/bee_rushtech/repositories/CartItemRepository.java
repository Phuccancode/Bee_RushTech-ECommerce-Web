package com.project.bee_rushtech.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.bee_rushtech.models.CartItem;
import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findAllByCartId(Long cartId); // Phương thức để tìm các mục giỏ hàng theo

    CartItem findByIdAndCartId(Long id, Long cartId); // Phương thức để tìm mục giỏ hàng theo id và cartId

    CartItem save(CartItem cartItem); // Phương thức để lưu mục giỏ hàng
}
