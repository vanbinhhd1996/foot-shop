package com.doitteam.foodstore.service;


import com.doitteam.foodstore.dto.request.CategoryRequest;
import com.doitteam.foodstore.dto.response.CategoryResponse;
import com.doitteam.foodstore.exception.ResourceNotFoundException;
import com.doitteam.foodstore.model.Category;
import com.doitteam.foodstore.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToCategoryResponse)
                .collect(Collectors.toList());
    }

    public List<CategoryResponse> getActiveCategories() {
        return categoryRepository.findByIsActiveTrue().stream()
                .map(this::mapToCategoryResponse)
                .collect(Collectors.toList());
    }

    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        return mapToCategoryResponse(category);
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        category.setDisplayOrder(request.getDisplayOrder());
        category.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent Category", "id", request.getParentId()));
            category.setParent(parent);
        }

        Category savedCategory = categoryRepository.save(category);
        log.info("Category created: {}", savedCategory.getName());

        return mapToCategoryResponse(savedCategory);
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        category.setDisplayOrder(request.getDisplayOrder());
        category.setIsActive(request.getIsActive());

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent Category", "id", request.getParentId()));
            category.setParent(parent);
        }

        Category updatedCategory = categoryRepository.save(category);
        log.info("Category updated: {}", updatedCategory.getName());

        return mapToCategoryResponse(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        categoryRepository.delete(category);
        log.info("Category deleted: {}", category.getName());
    }

    private CategoryResponse mapToCategoryResponse(Category category) {
        List<CategoryResponse> children = category.getChildren().stream()
                .map(this::mapToCategoryResponse)
                .collect(Collectors.toList());

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getParent() != null ? category.getParent().getId() : null,
                category.getImageUrl(),
                category.getDisplayOrder(),
                category.getIsActive(),
                children,
                category.getCreatedAt()
        );
    }
}