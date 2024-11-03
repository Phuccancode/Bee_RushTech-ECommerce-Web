package com.project.bee_rushtech.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.bee_rushtech.models.OrderItem;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Tìm tất cả các mục của một đơn hàng cụ thể
    List<OrderItem> findByOrderId(Long orderId);

    // Tìm tất cả các mục có sản phẩm cụ thể trong các đơn hàng
    List<OrderItem> findByProductId(Long productId);
}
