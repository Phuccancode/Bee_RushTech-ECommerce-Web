package com.project.bee_rushtech.controllers;

import com.project.bee_rushtech.models.CartItem;
import com.project.bee_rushtech.models.Order;
import com.project.bee_rushtech.services.CartService;
import com.project.bee_rushtech.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/customer")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @PostMapping("/order")
    public ResponseEntity<Order> createOrder(@RequestParam Long userId) {
        // Lấy các CartItem của người dùng từ CartService
        List<CartItem> cartItems = cartService.getAllCartItems(userId);
        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body(null); // Kiểm tra xem giỏ hàng có trống không
        }

        // Gọi OrderService để tạo một Order mới từ CartItem
        Order order = orderService.createOrderFromCart(cartItems, userId);
        return ResponseEntity.ok(order);
    }
}
