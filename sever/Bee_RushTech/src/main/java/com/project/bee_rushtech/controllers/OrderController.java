package com.project.bee_rushtech.controllers;

import com.project.bee_rushtech.dtos.OrderDTO;
import com.project.bee_rushtech.responses.OrderResponse;
import com.project.bee_rushtech.services.IOrderService;
import com.project.bee_rushtech.utils.SecurityUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;
    private final SecurityUtil securityUtil;

    @PostMapping("")
    public ResponseEntity<?> createOrder(@RequestBody @Valid OrderDTO orderDTO,
            BindingResult result, @CookieValue(name = "refresh_token", defaultValue = "") String token) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            if (token.equals("")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized");
            }
            Long userId = this.securityUtil.getUserFromToken(token).getId();
            orderDTO.setUserId(userId);
            OrderResponse orderResponse = orderService.createOrder(orderDTO);
            return ResponseEntity.ok(orderResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getOrders(@CookieValue(name = "refresh_token", defaultValue = "") String token) {
        try {
            if (token.equals("")) {
                return ResponseEntity.badRequest().body("You are not authorized");
            }
            Long userId = this.securityUtil.getUserFromToken(token).getId();
            List<OrderResponse> orderResponses = orderService.findByUserId(userId);
            return ResponseEntity.ok(orderResponses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@PathVariable("id") Long orderId,
            @CookieValue(name = "refresh_token", defaultValue = "") String token) {
        try {
            if (token.equals("")) {
                return ResponseEntity.badRequest().body("You are not authorized");
            }
            Long userId = this.securityUtil.getUserFromToken(token).getId();

            return ResponseEntity.ok(orderService.findByOrderIdAndUserId(orderId, userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    // admin works
    public ResponseEntity<?> updateOrder(@PathVariable Long id,
            @Valid @RequestBody OrderDTO orderDTO) {
        try {
            OrderResponse orderResponse = orderService.updateOrder(id, orderDTO);
            return ResponseEntity.ok(orderResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@Valid @PathVariable Long id) {
        // soft delete -> active = false
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.ok("Order deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // TO DO Get: get list order from user id
    // method getOders
    // TO Do Put: update info of a order from user id
    // method: updateOrder
    // admin work
    // To do Delete from user id
    // method: deleteOrder
    // Xóa mềm active =false
}
