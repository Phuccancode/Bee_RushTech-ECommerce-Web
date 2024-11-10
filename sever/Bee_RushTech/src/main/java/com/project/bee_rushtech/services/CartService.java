package com.project.bee_rushtech.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.javafaker.Cat;
import com.project.bee_rushtech.models.Cart;
import com.project.bee_rushtech.models.CartItem;
import com.project.bee_rushtech.models.Product;
import com.project.bee_rushtech.repositories.CartItemRepository;
import com.project.bee_rushtech.repositories.CartRepository;
import com.project.bee_rushtech.repositories.ProductRepository;
import java.util.Optional;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    public CartService(CartRepository cartRepository, ProductRepository productRepository,
            CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public Cart addProductToCart(Long cartId, Long productId, Integer quantity) {
        Optional<Product> productOpt = productRepository.findById(productId);
        Optional<Cart> cartOpt = cartRepository.findById(cartId);

        if (productOpt.isEmpty() || cartOpt.isEmpty()) {
            throw new RuntimeException("Product or Cart not found!");
        }

        CartItem cartItem = new CartItem();
        cartItem.setCart(cartOpt.get());
        cartItem.setProduct(productOpt.get());
        cartItem.setQuantity(quantity);

        cartItemRepository.save(cartItem);
        return cartOpt.get();
    }

    public Cart updateCartItem(Long cartItemId, Integer quantity) {
        Optional<CartItem> cartItemOpt = cartItemRepository.findById(cartItemId);

        if (cartItemOpt.isEmpty()) {
            throw new RuntimeException("Cart item not found!");
        }

        CartItem cartItem = cartItemOpt.get();
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);

        return cartItem.getCart();
    }

    public void removeProductFromCart(Long cartItemId) {
        Optional<CartItem> cartItemOpt = cartItemRepository.findById(cartItemId);

        if (cartItemOpt.isEmpty()) {
            throw new RuntimeException("Cart item not found!");
        }

        cartItemRepository.delete(cartItemOpt.get());
    }

    public List<CartItem> getAllCartItems(Long userId) {
        return cartItemRepository.findAllById(userId);
    }
}
