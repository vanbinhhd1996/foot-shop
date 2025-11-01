package com.doitteam.foodstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
class UpdateOrderStatusRequest {
    @NotBlank(message = "Status is required")
    private String status;

    private String note;
}