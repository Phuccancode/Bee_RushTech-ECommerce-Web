package com.project.bee_rushtech.services;

import com.project.bee_rushtech.dtos.OrderDetailDTO;
import com.project.bee_rushtech.models.CartItem;
import com.project.bee_rushtech.repositories.CartItemRepository;
import com.project.bee_rushtech.utils.errors.*;
import com.project.bee_rushtech.models.Order;
import com.project.bee_rushtech.models.OrderDetail;
import com.project.bee_rushtech.models.Product;
import com.project.bee_rushtech.repositories.OrderDetailRepository;
import com.project.bee_rushtech.repositories.OrderRepository;
import com.project.bee_rushtech.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        OrderDetail orderDetail = OrderDetail.builder()
                .order(order)
                .product(product)
                .price(product.getPrice())
                .totalMoney(product.getPrice()*cartItem.getQuantity())
                .returnDate(newOrderDetail.getReturnDate())
                .numberOfProducts(cartItem.getQuantity())
                .build();
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
}
