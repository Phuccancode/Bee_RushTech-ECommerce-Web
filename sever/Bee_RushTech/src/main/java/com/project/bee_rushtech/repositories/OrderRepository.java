package com.project.bee_rushtech.repositories;

import com.project.bee_rushtech.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Có thể thêm các phương thức tìm kiếm tùy chỉnh nếu cần
}
