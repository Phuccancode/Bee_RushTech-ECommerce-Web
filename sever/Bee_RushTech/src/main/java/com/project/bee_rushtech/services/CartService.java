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
import com.project.bee_rushtech.utils.errors.InvalidException;

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

    public void createCart(Long userId) {
        Cart cart = new Cart();
        cart.setId(userId);
        cartRepository.save(cart);
    }

    public CartItem addProductToCart(Long cartId, Long productId, Long quantity) {
        Optional<Product> productOpt = productRepository.findById(productId);
        Optional<Cart> cartOpt = cartRepository.findById(cartId);

        if (productOpt.isEmpty() || cartOpt.isEmpty()) {
            throw new RuntimeException("Product or Cart not found!");
        }

        CartItem cartItem = new CartItem();
        cartItem.setCart(cartOpt.get());
        cartItem.setProduct(productOpt.get());
        cartItem.setQuantity(quantity);

        return this.cartItemRepository.save(cartItem);
    }

    public void updateCartItem(Long cartId, Long cartItemId, Long quantity) throws InvalidException {
        CartItem cartItem = cartItemRepository.findByIdAndCartId(cartItemId, cartId);
        System.out.println(" cartId: " + cartId + " cartItemId: " + cartItemId);
        if (cartItem == null) {
            throw new InvalidException("Cart item not found!");
        }
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
    }

    public void removeProductFromCart(Long cartItemId) {
        Optional<CartItem> cartItemOpt = cartItemRepository.findById(cartItemId);

        if (cartItemOpt.isEmpty()) {
            throw new RuntimeException("Cart item not found!");
        }

        cartItemRepository.delete(cartItemOpt.get());
    }

    public List<CartItem> getAllCartItems(Long cartId) {
        return cartItemRepository.findAllByCartId(cartId);
    }

    public boolean existsByUserId(Long userId) {
        return cartRepository.existsByUserId(userId);
    }

    public Cart getByUserId(Long userId) {
        return cartRepository.findByUserId(userId);
    }
}
