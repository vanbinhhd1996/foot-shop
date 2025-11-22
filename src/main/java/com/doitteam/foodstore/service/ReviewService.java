package com.doitteam.foodstore.service;


import com.doitteam.foodstore.dto.request.CreateReviewRequest;
import com.doitteam.foodstore.dto.response.ReviewResponse;
import com.doitteam.foodstore.exception.BadRequestException;
import com.doitteam.foodstore.exception.ResourceNotFoundException;
import com.doitteam.foodstore.model.Product;
import com.doitteam.foodstore.model.Review;
import com.doitteam.foodstore.model.User;
import com.doitteam.foodstore.repository.ProductRepository;
import com.doitteam.foodstore.repository.ReviewRepository;
import com.doitteam.foodstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public List<ReviewResponse> getProductReviews(Long productId) {
        return reviewRepository.findByProductId(productId).stream()
                .map(this::mapToReviewResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReviewResponse createReview(Long userId, CreateReviewRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (reviewRepository.findByProductIdAndUserId(request.getProductId(), userId).isPresent()) {
            throw new BadRequestException("You have already reviewed this product");
        }

        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setIsVerified(false);

        Review savedReview = reviewRepository.save(review);
        log.info("Review created for product: {} by user: {}", product.getName(), userId);

        return mapToReviewResponse(savedReview);
    }

    @Transactional
    public void deleteReview(Long userId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

        if (!review.getUser().getId().equals(userId)) {
            throw new BadRequestException("This review does not belong to you");
        }

        reviewRepository.delete(review);
        log.info("Review deleted by user: {}", userId);
    }

    private ReviewResponse mapToReviewResponse(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getProduct().getId(),
                review.getUser().getId(),
                review.getUser().getUsername(),
                review.getRating(),
                review.getComment(),
                review.getIsVerified(),
                review.getCreatedAt()
        );
    }
}