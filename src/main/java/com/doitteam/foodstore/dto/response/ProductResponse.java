package com.doitteam.foodstore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
class ProductResponse {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private String imageUrl;
    private String sku;
    private Boolean isActive;
    private Integer views;
    private Double averageRating;
    private Long reviewCount;
    private List<String> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}