package com.project.bee_rushtech.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.project.bee_rushtech.models.Cart;
import com.project.bee_rushtech.services.CartService;

@RestController
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/customer/cart")
    public ResponseEntity<Cart> addProductToCart(@RequestParam Long cartId,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        Cart cart = cartService.addProductToCart(cartId, productId, quantity);
        return ResponseEntity.ok(cart);
    }

    @PutMapping("customer/cart/{id}")
    public ResponseEntity<Cart> updateCartItem(@PathVariable Long id,
            @RequestParam Integer quantity) {
        Cart cart = cartService.updateCartItem(id, quantity);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("customer/cart/{id}")
    public ResponseEntity<Void> removeProductFromCart(@PathVariable Long id) {
        cartService.removeProductFromCart(id);
        return ResponseEntity.noContent().build();
    }
}
