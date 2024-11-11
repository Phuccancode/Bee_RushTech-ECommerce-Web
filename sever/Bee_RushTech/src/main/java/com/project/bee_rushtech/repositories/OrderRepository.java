package com.project.bee_rushtech.repositories;

import com.project.bee_rushtech.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    //Tìm các đơn hàng của 1 user nào đó
    List<Order> findByUserId(Long userId);

}