package com.project.bee_rushtech.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.github.javafaker.Cat;
import com.project.bee_rushtech.dtos.CartItemDTO;
import com.project.bee_rushtech.models.Cart;
import com.project.bee_rushtech.models.CartItem;
import com.project.bee_rushtech.models.User;
import com.project.bee_rushtech.responses.AddItemToCartResponse;
import com.project.bee_rushtech.responses.CartItemResponse;
import com.project.bee_rushtech.services.CartService;
import com.project.bee_rushtech.services.UserService;
import com.project.bee_rushtech.utils.SecurityUtil;
import com.project.bee_rushtech.utils.annotation.ApiMessage;
import com.project.bee_rushtech.utils.errors.InvalidException;

@RestController
@RequestMapping("${api.prefix}/customer")
public class CartController {

    private final CartService cartService;
    private final SecurityUtil securityUtil;
    private final UserService userService;

    public CartController(CartService cartService, SecurityUtil securityUtil, UserService userService) {
        this.cartService = cartService;
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    @PostMapping("/cart")
    public ResponseEntity<AddItemToCartResponse> addProductToCart(@RequestBody CartItemDTO cartItemDTO,
            @CookieValue(name = "refresh_token") String token) {

        Jwt tokenDecoded = this.securityUtil.checkValidRefreshToken(token);
        String userId_String = tokenDecoded.getId();
        Long userId = Long.parseLong(userId_String);
        if (this.cartService.existsByUserId(userId) == false) {
            this.cartService.createCart(userId);
        }
        Cart currenCart = this.cartService.getByUserId(userId);
        Long cartId = currenCart.getId();
        this.cartService.addProductToCart(cartId, cartItemDTO.getProductId(),
                cartItemDTO.getQuantity());

        AddItemToCartResponse res = new AddItemToCartResponse(cartId, cartItemDTO.getProductId(),
                cartItemDTO.getQuantity());
        return ResponseEntity.ok(res);
    }

    @GetMapping("/cart")
    public ResponseEntity<List<CartItemResponse>> getAllCarts(@CookieValue(name = "refresh_token") String token) {
        Jwt tokenDecoded = this.securityUtil.checkValidRefreshToken(token);
        String userId_String = tokenDecoded.getId();
        Long userId = Long.parseLong(userId_String);
        Long CartId = cartService.getByUserId(userId).getId();
        List<CartItem> cart = cartService.getAllCartItems(CartId);

        List<CartItemResponse> cartResponse = new ArrayList<>();
        for (CartItem item : cart) {
            CartItemResponse res = new CartItemResponse();
            res.setId(item.getId());
            res.setProductId(item.getProduct().getId());
            res.setQuantity(item.getQuantity());
            cartResponse.add(res);
        }

        return ResponseEntity.status(HttpStatus.OK).body(cartResponse);
    }

    @PutMapping("/cart")
    public ResponseEntity<CartItemResponse> updateCartItem(@CookieValue(name = "refresh_token") String token,
            @RequestBody CartItemDTO cartItemDTO) throws InvalidException {
        Jwt tokenDecoded = this.securityUtil.checkValidRefreshToken(token);
        String userId_String = tokenDecoded.getId();
        Long userId = Long.parseLong(userId_String);
        Long CartId = cartService.getByUserId(userId).getId();
        this.cartService.updateCartItem(CartId, cartItemDTO.getId(), cartItemDTO.getQuantity());
        CartItemResponse cartResponse = new CartItemResponse();
        cartResponse.setId(cartItemDTO.getId());
        cartResponse.setQuantity(cartItemDTO.getQuantity());
        return ResponseEntity.ok(cartResponse);
    }

    @DeleteMapping("/cart")
    @ApiMessage("Delete product from cart successfully")
    public ResponseEntity<Void> removeProductFromCart(@RequestParam Long id) {
        cartService.removeProductFromCart(id);
        return ResponseEntity.ok().build();
    }
}
