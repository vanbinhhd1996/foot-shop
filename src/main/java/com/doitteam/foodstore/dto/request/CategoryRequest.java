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
public class CategoryRequest {
    @NotBlank(message = "Category name is required")
    private String name;

    private String description;
    private Long parentId;
    private String imageUrl;
    private Integer displayOrder;
    private Boolean isActive;
}
