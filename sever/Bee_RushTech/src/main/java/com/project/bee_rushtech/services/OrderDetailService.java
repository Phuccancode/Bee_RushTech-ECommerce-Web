package com.project.bee_rushtech.services;

import com.project.bee_rushtech.dtos.OrderDetailDTO;
import com.project.bee_rushtech.models.*;
import com.project.bee_rushtech.repositories.CartItemRepository;
import com.project.bee_rushtech.utils.errors.*;
import com.project.bee_rushtech.repositories.OrderDetailRepository;
import com.project.bee_rushtech.repositories.OrderRepository;
import com.project.bee_rushtech.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


@RequiredArgsConstructor
@Service
public class OrderDetailService implements IOrderDetailService{
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CartItemRepository cartItemRepository;
    @Override
    public OrderDetail createOrderDetail(OrderDetailDTO newOrderDetail) throws Exception {
        CartItem cartItem = cartItemRepository
                .findById(newOrderDetail.getCartItemId())
                .orElseThrow(() ->
                        new DataNotFoundException("Cart item not found with id "+newOrderDetail.getCartItemId()));
        Order order = orderRepository
                .findById(newOrderDetail.getOrderId())
                .orElseThrow(() ->
                        new DataNotFoundException("Order not found with id "+newOrderDetail.getOrderId()));
        Product product = productRepository
                .findById(cartItem.getProduct().getId())
                .orElseThrow(() ->
                        new DataNotFoundException("Product not found with id "+cartItem.getProduct().getId()));

        User user = order.getUser();

        OrderDetail orderDetail = OrderDetail.builder()
                .order(order)
                .product(product)
                .price(product.getPrice())
                .totalMoney(product.getPrice()*cartItem.getQuantity())
                .returnDateTime(newOrderDetail.getReturnDateTime())
                .numberOfProducts(cartItem.getQuantity())
                .build();

        // Xử lý khuyến mãi
        // First time buy
        long hoursDifference =
                Duration
                        .between(orderDetail.getReturnDateTime(),LocalDateTime.now())
                        .toHours();
        if(orderRepository.countByUserId(order.getUser().getId()) ==1){
            if(hoursDifference < 5){
                orderDetail.setReturnDateTime(orderDetail.getReturnDateTime().plusHours(1));
            }
            else{
                orderDetail.setReturnDateTime(orderDetail.getReturnDateTime().plusHours(3));
            }
        }else if(orderRepository.countByUserId(order.getUser().getId())>1
                &&hoursDifference >=8
        ){
            orderDetail.setReturnDateTime(orderDetail.getReturnDateTime().plusHours(1));
        }
        if(isEduEmail(user.getEmail())){
            if(hoursDifference < 5*24){
                orderDetail.setReturnDateTime(orderDetail
                        .getReturnDateTime()
                        .plusHours(hoursDifference/4)
                );
            }
            else{
                orderDetail.setReturnDateTime(orderDetail
                        .getReturnDateTime()
                        .plusHours(2*24)
                );
            }
        }
        cartItemRepository.delete(cartItem);
        return orderDetailRepository.save(orderDetail);
    }


    @Override
    public OrderDetail getOrderDetail(Long id) throws Exception {
        return orderDetailRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Order detail not found with id "+id));
    }

//    @Override
//    public OrderDetail updateOrderDetail(Long id, OrderDetailDTO newOrderDetailDTO) throws Exception {
//        Order order = orderRepository
//                .findById(newOrderDetailDTO.getOrderId())
//                .orElseThrow(() -> new DataNotFoundException("Order not found with id "+newOrderDetailDTO.getOrderId()));
//        Product product = productRepository.findById(newOrderDetailDTO.getProductId())
//                .orElseThrow(() -> new DataNotFoundException("Product not found with id "+newOrderDetailDTO.getProductId()));
//        return orderDetailRepository.findById(id)//Hàm map này của optional
//                .map(orderDetail -> {
//                    orderDetail.setPrice(newOrderDetailDTO.getPrice());
//                    orderDetail.setNumberOfProducts(newOrderDetailDTO.getNumberOfProduct());
//                    orderDetail.setTotalMoney(newOrderDetailDTO.getTotalMoney());
//                    orderDetail.setReturnDate(newOrderDetailDTO.getReturnDate());
//                    orderDetail.setOrder(order);
//                    orderDetail.setProduct(product);
//                    return orderDetailRepository.save(orderDetail);
//                }).orElseThrow(() -> new DataNotFoundException("Order detail not found with id "+id));
//
//    }

    @Override
    public void deleteOrderDetail(Long id)  {
        orderDetailRepository.deleteById(id);
    }

    @Override
    public List<OrderDetail> findByOrderId(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }
    private boolean isEduEmail(String email) {
        try {
            // Tách domain từ email (phần sau @)
            String domain = email.substring(email.indexOf("@") + 1);

            // Kiểm tra xem domain có chứa ".edu"
            return domain.contains(".edu");
        } catch (Exception e) {
            return false; // Trả về false nếu xảy ra lỗi (vd: email không hợp lệ)
        }
    }
}
