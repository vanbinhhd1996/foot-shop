package com.doitteam.foodstore.dto.request;
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
class CreateOrderRequest {
    @NotBlank(message = "Shipping name is required")
    private String shippingName;

    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;

    @NotBlank(message = "Shipping phone is required")
    private String shippingPhone;

    @NotNull(message = "Payment method is required")
    private String paymentMethod;

    private String note;
}