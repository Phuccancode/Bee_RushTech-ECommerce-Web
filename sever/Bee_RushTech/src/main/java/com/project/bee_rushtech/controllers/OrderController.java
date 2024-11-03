package com.project.bee_rushtech.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.project.bee_rushtech.models.Order;
import com.project.bee_rushtech.models.CartItem;
import com.project.bee_rushtech.services.OrderService;
import com.project.bee_rushtech.services.CartService;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @PostMapping("/create/{userId}")
    public ResponseEntity<Order> createOrder(@PathVariable Long userId) {
        List<CartItem> cartItems = cartService.getCartItemsByUserId(userId);
        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        Order order = orderService.createOrderFromCart(cartItems, userId);
        return ResponseEntity.ok(order);
    }
}
