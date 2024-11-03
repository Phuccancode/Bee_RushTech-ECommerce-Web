package com.project.bee_rushtech.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.project.bee_rushtech.models.Order;
import com.project.bee_rushtech.models.OrderItem;
import com.project.bee_rushtech.models.CartItem;
import com.project.bee_rushtech.models.User;
import com.project.bee_rushtech.repositories.OrderRepository;
import com.project.bee_rushtech.repositories.OrderItemRepository;

import java.util.Date;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserService userService; // Thêm UserService nếu bạn có

    public Order createOrderFromCart(List<CartItem> cartItems, Long userId) {
        Order order = new Order();
        
        // Lấy đối tượng User từ UserService
        User user = userService.findById(userId); // UserService sẽ lấy User từ DB
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        order.setUser(user);
        order.setOrderDate(new Date());
        order.setStatus("Pending");
        order = orderRepository.save(order);

        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice((double) cartItem.getProduct().getPrice());

            orderItemRepository.save(orderItem);
        }
        
        return order;
    }
}
