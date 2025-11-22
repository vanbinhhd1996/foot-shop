package com.doitteam.foodstore.service;


import com.doitteam.foodstore.dto.request.CreateOrderRequest;
import com.doitteam.foodstore.dto.request.UpdateOrderStatusRequest;
import com.doitteam.foodstore.dto.response.OrderItemResponse;
import com.doitteam.foodstore.dto.response.OrderListResponse;
import com.doitteam.foodstore.dto.response.OrderResponse;
import com.doitteam.foodstore.exception.BadRequestException;
import com.doitteam.foodstore.exception.InsufficientStockException;
import com.doitteam.foodstore.exception.ResourceNotFoundException;
import com.doitteam.foodstore.exception.UnauthorizedException;
import com.doitteam.foodstore.model.*;
import com.doitteam.foodstore.model.enums.OrderStatus;
import com.doitteam.foodstore.model.enums.PaymentMethod;
import com.doitteam.foodstore.model.enums.PaymentStatus;
import com.doitteam.foodstore.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderStatusHistoryRepository statusHistoryRepository;

    @Transactional
    public OrderResponse createOrder(Long userId, CreateOrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user"));

        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new InsufficientStockException(
                        "Product " + product.getName() + " has insufficient stock"
                );
            }
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderNumber(generateOrderNumber());
        order.setTotalAmount(cart.getTotalAmount());
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentMethod(PaymentMethod.valueOf(request.getPaymentMethod()));
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setShippingName(request.getShippingName());
        order.setShippingAddress(request.getShippingAddress());
        order.setShippingPhone(request.getShippingPhone());
        order.setShippingFee(BigDecimal.ZERO);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setNote(request.getNote());

        Order savedOrder = orderRepository.save(order);

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.calculateSubtotal();

            orderItemRepository.save(orderItem);

            product.decreaseStock(cartItem.getQuantity());
            productRepository.save(product);
        }

        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(savedOrder);
        history.setOldStatus(null);
        history.setNewStatus(OrderStatus.PENDING.name());
        history.setNote("Order created");
        history.setChangedBy(user);
        statusHistoryRepository.save(history);

        cartItemRepository.deleteByCartId(cart.getId());

        log.info("Order created: {} for user: {}", savedOrder.getOrderNumber(), userId);

        return mapToOrderResponse(savedOrder);
    }

    public List<OrderListResponse> getOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return orders.stream()
                .map(this::mapToOrderListResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (!order.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("This order does not belong to you");
        }

        return mapToOrderResponse(order);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        OrderStatus oldStatus = order.getStatus();
        OrderStatus newStatus = OrderStatus.valueOf(request.getStatus());

        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(updatedOrder);
        history.setOldStatus(oldStatus.name());
        history.setNewStatus(newStatus.name());
        history.setNote(request.getNote());
        history.setChangedBy(user);
        statusHistoryRepository.save(history);

        log.info("Order status updated: {} from {} to {}", order.getOrderNumber(), oldStatus, newStatus);

        return mapToOrderResponse(updatedOrder);
    }

    @Transactional
    public void cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (!order.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("This order does not belong to you");
        }

        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CONFIRMED) {
            throw new BadRequestException("Cannot cancel order in current status");
        }

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.increaseStock(item.getQuantity());
            productRepository.save(product);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setOldStatus(oldStatus.name());
        history.setNewStatus(OrderStatus.CANCELLED.name());
        history.setNote("Order cancelled by user");
        history.setChangedBy(user);
        statusHistoryRepository.save(history);

        log.info("Order cancelled: {} by user: {}", order.getOrderNumber(), userId);
    }

    private String generateOrderNumber() {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "ORD-" + timestamp + "-" + (int)(Math.random() * 1000);
    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> items = order.getOrderItems().stream()
                .map(this::mapToOrderItemResponse)
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getTotalAmount(),
                order.getStatus().name(),
                order.getPaymentMethod().name(),
                order.getPaymentStatus().name(),
                order.getShippingAddress(),
                order.getShippingPhone(),
                order.getShippingName(),
                order.getShippingFee(),
                order.getDiscountAmount(),
                order.calculateFinalAmount(),
                order.getNote(),
                items,
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }

    private OrderListResponse mapToOrderListResponse(Order order) {
        return new OrderListResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getTotalAmount(),
                order.getStatus().name(),
                order.getPaymentStatus().name(),
                order.getOrderItems().size(),
                order.getCreatedAt()
        );
    }

    private OrderItemResponse mapToOrderItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProductName(),
                item.getQuantity(),
                item.getPrice(),
                item.getSubtotal()
        );
    }
}