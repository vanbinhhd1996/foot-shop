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
public class OrderListResponse {
    private Long id;
    private String orderNumber;
    private BigDecimal totalAmount;
    private String status;
    private String paymentStatus;
    private Integer itemCount;
    private LocalDateTime createdAt;
}