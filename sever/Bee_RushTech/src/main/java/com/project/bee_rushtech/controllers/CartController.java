package com.project.bee_rushtech.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.project.bee_rushtech.models.Cart;
import com.project.bee_rushtech.models.CartItem;
import com.project.bee_rushtech.services.CartService;

@RestController
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/customer/cart")
    public ResponseEntity<Cart> addProductToCart(@RequestParam Long cartId,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        Cart cart = cartService.addProductToCart(cartId, productId, quantity);
        return ResponseEntity.ok(cart);
    }

    @GetMapping("/customer/cart")
    public ResponseEntity<List<CartItem>> getAllCarts(@RequestParam Long userId) {
        List<CartItem> cart = cartService.getAllCartItems(userId);
        return ResponseEntity.status(HttpStatus.OK).body(cart);
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
