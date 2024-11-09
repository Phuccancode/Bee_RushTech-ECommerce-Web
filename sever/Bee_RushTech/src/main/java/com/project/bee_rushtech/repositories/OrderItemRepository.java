package com.project.bee_rushtech.repositories;

import com.project.bee_rushtech.models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // Có thể thêm các phương thức tìm kiếm tùy chỉnh nếu cần
}
