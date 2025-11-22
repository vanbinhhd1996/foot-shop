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
public class ProductListResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private String imageUrl;
    private Integer stockQuantity;
    private Boolean isActive;
    private Double averageRating;
}