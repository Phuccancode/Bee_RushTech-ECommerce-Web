package com.project.bee_rushtech.services;

import com.project.bee_rushtech.dtos.OrderDTO;
import com.project.bee_rushtech.responses.OrderResponse;

import java.util.List;

public interface IOrderService {
    OrderResponse createOrder(OrderDTO orderDTO) throws Exception;

    OrderResponse getOrder(Long orderId) throws Exception;

    OrderResponse updateOrder(Long id, OrderDTO orderDTO) throws Exception;

    void deleteOrder(Long orderId) throws Exception;

    List<OrderResponse> findByUserId(Long userId);
}
