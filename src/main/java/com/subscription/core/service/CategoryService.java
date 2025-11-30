package com.subscription.core.service;

import com.subscription.core.api.SearchCategoryApi;
import com.subscription.core.dto.CategorySearchDTO;
import com.subscription.core.dto.CategorySearchResponseDTO;
import com.subscription.core.dto.CategoryUpsertDTO;
import com.subscription.core.entity.Category;
import com.subscription.core.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

/**
 * Service for managing categories.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final SearchCategoryApi searchCategoryApi;

    /**
     * Creates or updates a category based on the provided request.
     *
     * @param request The category data to create or update
     * @return Success message with the category ID
     * @throws IllegalArgumentException if category not found during update
     */
    @Transactional
    public String upsertCategory(CategoryUpsertDTO request) {
        log.info("[f:upsertCategory] Processing category upsert: {}", request.getCategoryId());
        
        Category category = getOrCreateCategory(request);
        updateCategoryFields(category, request);
        
        categoryRepository.save(category);
        log.info("[f:upsertCategory] Category saved with ID: {}", category.getCategoryId());
        return "Category upserted successfully with ID: " + category.getCategoryId();
    }

    /**
     * Retrieves existing category or creates a new one.
     *
     * @param request The upsert request
     * @return Category entity
     * @throws IllegalArgumentException if category not found during update
     */
    private Category getOrCreateCategory(CategoryUpsertDTO request) {
        if (Objects.nonNull(request.getCategoryId())) {
            return categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Category not found with ID: " + request.getCategoryId()));
        }
        return new Category();
    }

    /**
     * Updates category fields from the request.
     *
     * @param category The entity to update
     * @param request The request containing new values
     */
    private void updateCategoryFields(Category category, CategoryUpsertDTO request) {
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        category.setGstSlab(request.getGstSlab());
        category.setDisplayOrder(request.getDisplayOrder());
        if (Objects.nonNull(request.getIsActive())) {
            category.setIsActive(request.getIsActive());
        }
    }

    /**
     * Searches categories using the search API.
     *
     * @param searchDTO The search criteria
     * @return Search response with filtered categories
     */
    public CategorySearchResponseDTO searchCategories(CategorySearchDTO searchDTO) {
        log.info("[f:searchCategories] Searching categories with criteria: {}", searchDTO);
        return searchCategoryApi.search(searchDTO);
    }
}
