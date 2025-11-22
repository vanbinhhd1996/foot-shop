package com.doitteam.foodstore.controller;

import com.doitteam.foodstore.dto.request.*;
import com.doitteam.foodstore.dto.response.*;
import com.doitteam.foodstore.dto.response.ApiResponse;
import com.doitteam.foodstore.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @RequestParam Long userId,
            @Valid @RequestBody CreateOrderRequest request) {
        OrderResponse order = orderService.createOrder(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order created successfully", order));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderListResponse>>> getMyOrders(
            @RequestParam Long userId) {
        List<OrderListResponse> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @RequestParam Long userId,
            @PathVariable Long id) {
        OrderResponse order = orderService.getOrderById(userId, id);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @RequestParam Long userId,
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        OrderResponse order = orderService.updateOrderStatus(id, request, userId);
        return ResponseEntity.ok(ApiResponse.success("Order status updated", order));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(
            @RequestParam Long userId,
            @PathVariable Long id) {
        orderService.cancelOrder(userId, id);
        return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully", null));
    }
}