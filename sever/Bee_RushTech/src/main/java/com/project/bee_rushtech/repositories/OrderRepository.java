package com.project.bee_rushtech.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.bee_rushtech.models.Order;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Tìm tất cả các đơn hàng của một người dùng cụ thể
    List<Order> findByUserId(Long userId);

    // Tìm các đơn hàng theo trạng thái
    List<Order> findByStatus(String status);
}
