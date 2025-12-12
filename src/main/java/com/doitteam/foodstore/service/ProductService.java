package com.doitteam.foodstore.service;


import com.doitteam.foodstore.dto.request.ProductRequest;
import com.doitteam.foodstore.dto.response.ProductListResponse;
import com.doitteam.foodstore.dto.response.ProductResponse;
import com.doitteam.foodstore.exception.ResourceNotFoundException;
import com.doitteam.foodstore.model.Category;
import com.doitteam.foodstore.model.Product;
import com.doitteam.foodstore.repository.CategoryRepository;
import com.doitteam.foodstore.repository.ProductRepository;
import com.doitteam.foodstore.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ReviewRepository reviewRepository;

    public List<ProductListResponse> getAllProducts() {
        return productRepository.findByIsActiveTrue().stream()
                .map(this::mapToProductListResponse)
                .collect(Collectors.toList());
    }

    public List<ProductListResponse> getProductsByCategory(Long categoryId) {
        if (categoryId == 1 || categoryId == 2){
            List<Integer> categoryIds = categoryId == 1 ? List.of(9, 10 ,11): List.of(12, 13, 14, 15, 16);
            return productRepository.findByCategoryIds(categoryIds).stream()
                    .map(this::mapToProductListResponse)
                    .collect(Collectors.toList());
        }else return productRepository.findActiveByCategoryId(categoryId).stream()
                .map(this::mapToProductListResponse)
                .collect(Collectors.toList());

    }

    public List<ProductListResponse> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword).stream()
                .map(this::mapToProductListResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        product.increaseViews();
        productRepository.save(product);

        return mapToProductResponse(product);
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        Product product = new Product();
        product.setCategory(category);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setImageUrl(request.getImageUrl());
        product.setSku(request.getSku());
        product.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        Product savedProduct = productRepository.save(product);
        log.info("Product created: {}", savedProduct.getName());

        return mapToProductResponse(savedProduct);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        product.setCategory(category);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setImageUrl(request.getImageUrl());
        product.setSku(request.getSku());
        product.setIsActive(request.getIsActive());

        Product updatedProduct = productRepository.save(product);
        log.info("Product updated: {}", updatedProduct.getName());

        return mapToProductResponse(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        productRepository.delete(product);
        log.info("Product deleted: {}", product.getName());
    }

    private ProductResponse mapToProductResponse(Product product) {
        Double avgRating = reviewRepository.getAverageRatingByProductId(product.getId());
        Long reviewCount = reviewRepository.countByProductId(product.getId());

        return new ProductResponse(
                product.getId(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getImageUrl(),
                product.getSku(),
                product.getIsActive(),
                product.getViews(),
                avgRating,
                reviewCount,
                null,
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    private ProductListResponse mapToProductListResponse(Product product) {
        Double avgRating = reviewRepository.getAverageRatingByProductId(product.getId());

        return new ProductListResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getImageUrl(),
                product.getStockQuantity(),
                product.getIsActive(),
                avgRating
        );
    }
}