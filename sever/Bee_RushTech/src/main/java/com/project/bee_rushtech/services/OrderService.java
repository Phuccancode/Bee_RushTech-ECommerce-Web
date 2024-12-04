package com.project.bee_rushtech.services;

import com.project.bee_rushtech.dtos.HandleOrderDTO;
import com.project.bee_rushtech.dtos.OrderDTO;
import com.project.bee_rushtech.dtos.OrderDetailDTO;
import com.project.bee_rushtech.utils.errors.DataNotFoundException;
import com.project.bee_rushtech.models.Order;
import com.project.bee_rushtech.models.OrderDetail;
import com.project.bee_rushtech.models.OrderStatus;
import com.project.bee_rushtech.models.User;
import com.project.bee_rushtech.repositories.CartItemRepository;
import com.project.bee_rushtech.repositories.OrderRepository;
import com.project.bee_rushtech.repositories.UserRepository;
import com.project.bee_rushtech.responses.OrderResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
        private final UserRepository userRepository;
        private final OrderRepository orderRepository;
        private final ModelMapper modelMapper;
        private final OrderDetailService orderDetailService;
        private final CartItemRepository cartItemRepository;

        @Override
        public OrderResponse createOrder(OrderDTO orderDTO) throws Exception {
                User user = userRepository
                                .findById(orderDTO.getUserId())
                                .orElseThrow(() -> new DataNotFoundException(
                                                "User not found with id " + orderDTO.getUserId()));
                modelMapper.typeMap(OrderDTO.class, Order.class)
                                .addMappings(mapper -> mapper.skip(Order::setId));
                Order order = new Order();
                order = modelMapper.map(orderDTO, Order.class);
                order.setUser(user);
                order.setFullName(orderDTO.getFullName());
                order.setOrderDate(new Date());
                order.setStatus(OrderStatus.PENDING);

                // default 5 ngày kể từ ngày hiện tại
                // LocalDate shippingDate = orderDTO.getShippingDate() == null ?
                // LocalDate.now().plusDays(5)
                // : orderDTO.getShippingDate();
                // if (shippingDate.isBefore(LocalDate.now())) {
                // throw new DateTimeException("Invalid shipping date");
                // }
                order.setActive(true);

                order.setTrackingNumber(Order.generateTrackingNumber());

                List<OrderDetailDTO> orderDetailDTOS = orderDTO.getListOrderDetail();
                for (OrderDetailDTO orderDetailDTO : orderDetailDTOS) {
                        cartItemRepository.findById(orderDetailDTO.getCartItemId())
                                        .orElseThrow(() -> new DataNotFoundException(
                                                        "Cart item not found with id "
                                                                        + orderDetailDTO.getCartItemId()));
                }
                orderRepository.save(order);
                Float totalMoney = 0f;
                for (OrderDetailDTO orderDetailDTO : orderDetailDTOS) {
                        orderDetailDTO.setOrderId(order.getId());
                        OrderDetail orderDetail = orderDetailService.createOrderDetail(orderDetailDTO);
                        totalMoney += orderDetail.getTotalMoney();
                }
                order.setTotalMoney(totalMoney);
                orderRepository.save(order);

                return OrderResponse.fromOrder(order);
        }

        @Override
        public OrderResponse getOrder(Long orderId) throws Exception {
                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new DataNotFoundException("Order not found with id " + orderId));
                return OrderResponse.fromOrder(order);
        }

        @Override
        public OrderResponse updateOrder(Long id, OrderDTO orderDTO) throws Exception {
                Order order = orderRepository.findById(id)
                                .orElseThrow(() -> new DataNotFoundException("Order not found with id " + id));
                User user = userRepository.findById(orderDTO.getUserId())
                                .orElseThrow(() -> new DataNotFoundException(
                                                "User not found with id " + orderDTO.getUserId()));
                modelMapper.typeMap(OrderDTO.class, Order.class)
                                .addMappings(mapper -> mapper.skip(Order::setId));
                modelMapper.map(orderDTO, order);
                LocalDate shippingDate = orderDTO.getShippingDate() == null ? LocalDate.now().plusDays(5)
                                : orderDTO.getShippingDate();
                order.setUser(user);
                order.setShippingDate(shippingDate);
                orderRepository.save(order);
                return OrderResponse.fromOrder(order);
        }

        @Override
        public void deleteOrder(Long orderId) throws Exception {
                // soft delete
                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new DataNotFoundException("Order not found with id " + orderId));
                order.setActive(false);
                orderRepository.save(order);
        }

        @Override
        public List<OrderResponse> findByUserId(Long userId) {
                List<Order> orders = orderRepository.findByUserId(userId);
                List<OrderResponse> orderResponses = orders
                                .stream()
                                .map(OrderResponse::fromOrder)
                                .collect(Collectors.toList());
                // model mapper phai match all fields, neu co 1 filed khong match thi phai skip,
                // khong skip thi gia tri tra ve se bi null het
                return orderResponses;

        }

        @Override
        public OrderResponse findByOrderIdAndUserId(Long orderId, Long userId) throws Exception {
                Order order = orderRepository.findByIdAndUserId(orderId, userId);
                if (order == null) {
                        throw new DataNotFoundException("Order not found with id " + orderId);
                }
                return OrderResponse.fromOrder(order);
        }

        @Override
        public boolean checkOrderOwner(Long orderId, Long userId) {
                Order order = orderRepository.findByIdAndUserId(orderId, userId);
                return order != null;
        }

        @Override
        public void handleOrder(HandleOrderDTO handleOrderDTO) throws Exception {
                Order order = orderRepository.findById(handleOrderDTO.getOrderId())
                                .orElseThrow(() -> new DataNotFoundException(
                                                "Order not found with id " + handleOrderDTO.getOrderId()));

                order.setStatus(handleOrderDTO.getStatus());

                if (handleOrderDTO.getStatus().equals(OrderStatus.SHIPPING)) {
                        order.setShippingDate(LocalDate.now());
                        order.setTrackingNumber(Order.generateTrackingNumber());
                        order.setShippingMethod("GHN");
                } else if (handleOrderDTO.getStatus().equals(OrderStatus.RECEIVE)) {
                        List<OrderDetail> orderDetails = orderDetailService.findByOrderId(order.getId());
                        for (OrderDetail orderDetail : orderDetails) {
                                orderDetail.setReturnDateTime(
                                                LocalDateTime.now().plusHours(orderDetail.getTimeRenting()));
                        }
                }

                orderRepository.save(order);
        }

}
