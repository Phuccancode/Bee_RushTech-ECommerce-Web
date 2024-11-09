package com.project.bee_rushtech.services;

import com.project.bee_rushtech.models.CartItem;
import com.project.bee_rushtech.models.Order;
import com.project.bee_rushtech.models.OrderItem;
import com.project.bee_rushtech.models.User;
import com.project.bee_rushtech.repositories.OrderItemRepository;
import com.project.bee_rushtech.repositories.OrderRepository;
import com.project.bee_rushtech.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserRepository userRepository;

    public Order createOrderFromCart(List<CartItem> cartItems, Long userId) {
        // Lấy đối tượng User từ UserRepository
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Tạo đối tượng Order mới
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(new Date());
        order.setStatus("Pending");

        // Lưu đối tượng Order vào cơ sở dữ liệu
        order = orderRepository.save(order);

        // Tạo các OrderItem từ CartItem
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());

            // Lưu từng OrderItem vào cơ sở dữ liệu
            orderItemRepository.save(orderItem);
        }

        return order;
    }
}
