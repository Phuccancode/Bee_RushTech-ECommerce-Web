package com.project.bee_rushtech.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.bee_rushtech.models.CartItem;
import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findAllByCartId(Long cartId); // Phương thức để tìm các mục giỏ hàng theo

     // Phương thức để tìm mục giỏ hàng theo id

    CartItem findByProductIdAndCartId(Long id, Long cartId); // Phương thức để tìm mục giỏ hàng theo id và cartId

    CartItem save(CartItem cartItem); // Phương thức để lưu mục giỏ hàng

    CartItem findByProductId(Long productId); // Phương thức để tìm mục giỏ hàng theo productid

}
