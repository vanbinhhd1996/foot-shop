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
public class AddToCartRequest {
    @NotNull(message = "Product ID is required")
    private Long productId;

    @Min(value = 1)
    private Integer quantity;
}