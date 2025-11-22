package com.doitteam.foodstore.dto.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private BigDecimal totalAmount;
    private String status;
    private String paymentMethod;
    private String paymentStatus;
    private String shippingAddress;
    private String shippingPhone;
    private String shippingName;
    private BigDecimal shippingFee;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private String note;
    private List<OrderItemResponse> orderItems;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}