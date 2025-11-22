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
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private Long parentId;
    private String imageUrl;
    private Integer displayOrder;
    private Boolean isActive;
    private List<CategoryResponse> children;
    private LocalDateTime createdAt;
}