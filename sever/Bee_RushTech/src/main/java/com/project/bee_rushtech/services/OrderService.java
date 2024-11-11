package com.project.bee_rushtech.services;
import com.project.bee_rushtech.dtos.OrderDTO;
import com.project.bee_rushtech.utils.errors.DataNotFoundException;
import com.project.bee_rushtech.models.Order;
import com.project.bee_rushtech.models.OrderStatus;
import com.project.bee_rushtech.models.User;
import com.project.bee_rushtech.repositories.OrderRepository;
import com.project.bee_rushtech.repositories.UserRepository;
import com.project.bee_rushtech.responses.OrderResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService{
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;

    @Override
    public OrderResponse createOrder(OrderDTO orderDTO) throws Exception {
        User user = userRepository
                .findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("User not found with id "+orderDTO.getUserId()));
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        Order order = new Order();
        order = modelMapper.map(orderDTO, Order.class);
        order.setUser(user);
        order.setOrderDate(new Date());
        order.setStatus(OrderStatus.PENDING);

        //default 5 ngày kể từ ngày hiện tại
        LocalDate shippingDate = orderDTO.getShippingDate() == null ? LocalDate.now().plusDays(5)  : orderDTO.getShippingDate();
        if(shippingDate.isBefore(LocalDate.now())){
            throw new DateTimeException("Invalid shipping date");
        }
        order.setActive(true);
        order.setShippingDate(shippingDate);
        order.setTrackingNumber(Order.generateTrackingNumber());
        orderRepository.save(order);
        return OrderResponse.fromOrder(order);
    }

    @Override
    public OrderResponse getOrder(Long orderId) throws Exception{
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new DataNotFoundException("Order not found with id "+orderId));
        return OrderResponse.fromOrder(order);
    }

    @Override
    public OrderResponse updateOrder(Long id, OrderDTO orderDTO) throws Exception {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Order not found with id "+id));
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("User not found with id "+orderDTO.getUserId()));
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        modelMapper.map(orderDTO, order);
        LocalDate shippingDate = orderDTO.getShippingDate() == null ? LocalDate.now().plusDays(5)  : orderDTO.getShippingDate();
        order.setUser(user);
        order.setShippingDate(shippingDate);
        orderRepository.save(order);
        return OrderResponse.fromOrder(order);
    }

    @Override
    public void deleteOrder(Long orderId) throws Exception {
        //soft delete
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new DataNotFoundException("Order not found with id "+orderId));
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
        //model mapper phai match all fields, neu co 1 filed khong match thi phai skip,
        // khong skip thi gia tri tra ve se bi null het
        return orderResponses;

    }
}
