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
class ReviewResponse {
    private Long id;
    private Long productId;
    private Long userId;
    private String username;
    private Integer rating;
    private String comment;
    private Boolean isVerified;
    private LocalDateTime createdAt;
}