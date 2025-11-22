package com.doitteam.foodstore.controller;

import com.doitteam.foodstore.dto.request.*;
import com.doitteam.foodstore.dto.response.*;
import com.doitteam.foodstore.dto.response.ApiResponse;
import com.doitteam.foodstore.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getProductReviews(
            @PathVariable Long productId) {
        List<ReviewResponse> reviews = reviewService.getProductReviews(productId);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @RequestParam Long userId,
            @Valid @RequestBody CreateReviewRequest request) {
        ReviewResponse review = reviewService.createReview(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Review created successfully", review));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @RequestParam Long userId,
            @PathVariable Long id) {
        reviewService.deleteReview(userId, id);
        return ResponseEntity.ok(ApiResponse.success("Review deleted successfully", null));
    }
}