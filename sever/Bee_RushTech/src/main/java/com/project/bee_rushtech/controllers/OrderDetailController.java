package com.project.bee_rushtech.controllers;

import com.project.bee_rushtech.dtos.OrderDetailDTO;
import com.project.bee_rushtech.models.OrderDetail;
import com.project.bee_rushtech.responses.OrderDetailResponse;
import com.project.bee_rushtech.services.IOrderDetailService;
import com.project.bee_rushtech.services.IOrderService;
import com.project.bee_rushtech.services.OrderService;
import com.project.bee_rushtech.utils.SecurityUtil;
import com.project.bee_rushtech.utils.errors.InvalidException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/order_details")
@RequiredArgsConstructor
public class OrderDetailController {
    private final IOrderDetailService orderDetailService;
    private final SecurityUtil securityUtil;
    private final IOrderService orderService;

    @PostMapping("")
    public ResponseEntity<?> createOrderDetail(
            @Valid @RequestBody OrderDetailDTO orderDetailDTO) {
        try {
            OrderDetail orderDetail = orderDetailService.createOrderDetail(orderDetailDTO);

            return ResponseEntity.ok(OrderDetailResponse.fromOrderDetail(orderDetail));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetail(
            @Valid @PathVariable("id") Long id){
        try {
            OrderDetail orderDetail = orderDetailService.getOrderDetail(id);
            return ResponseEntity.ok(OrderDetailResponse.fromOrderDetail(orderDetail));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("order/{orderId}")
    public ResponseEntity<?> getOrderDetails(@CookieValue(name = "refresh_token", defaultValue = "") String token,
            @PathVariable("orderId") Long orderId) throws InvalidException {
        if (token.equals("")) {
            return ResponseEntity.badRequest().body("You are not authorized");
        }
        Long userId = this.securityUtil.getUserFromToken(token).getId();
        if (orderService.checkOrderOwner(userId, orderId)) {
            return ResponseEntity.badRequest().body("You are not authorized");
        }
        List<OrderDetailResponse> orderDetailResponses = orderDetailService.findByOrderId(orderId)
                .stream()
                .map(OrderDetailResponse::fromOrderDetail)
                .toList();

        return ResponseEntity.ok(orderDetailResponses);
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<?> updateOrderDetail(
//            @Valid @PathVariable("id") Long id,
//            @Valid @RequestBody OrderDetailDTO orderDetailDTO){
//        try{
//            OrderDetail orderDetail = orderDetailService.updateOrderDetail(id, orderDetailDTO);
//            return ResponseEntity.ok(OrderDetailResponse.fromOrderDetail(orderDetail));
//        }catch (Exception e){
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrderDetail(
            @Valid @PathVariable("id") Long id) {
        try {
            orderDetailService.deleteOrderDetail(id);
            return ResponseEntity.ok("deleted successfully with id " + id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
