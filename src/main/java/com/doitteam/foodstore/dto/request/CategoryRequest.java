package com.doitteam.foodstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
class CategoryRequest {
    @NotBlank(message = "Category name is required")
    private String name;

    private String description;
    private Long parentId;
    private String imageUrl;
    private Integer displayOrder;
    private Boolean isActive;
}