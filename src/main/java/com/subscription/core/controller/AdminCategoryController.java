package com.subscription.core.controller;

import com.subscription.core.dto.CategorySearchDTO;
import com.subscription.core.dto.CategorySearchResponseDTO;
import com.subscription.core.dto.CategoryUpsertDTO;
import com.subscription.core.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Admin controller for managing categories in production environment.
 */
@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
@Slf4j
public class AdminCategoryController {

    private final CategoryService categoryService;

    /**
     * Creates or updates a category.
     *
     * @param request The category data to create or update
     * @return Response containing success message
     */
    @PostMapping("/upsert")
    public ResponseEntity<String> upsertCategory(@Valid @RequestBody CategoryUpsertDTO request) {
        log.info("[f:upsertCategory] Processing admin category upsert request: {}", request);
        return ResponseEntity.ok(categoryService.upsertCategory(request));
    }

    /**
     * Searches categories based on provided filters.
     *
     * @param name Category name filter
     * @param isActive Active status filter
     * @param pageNo Page number for pagination
     * @param pageSize Page size for pagination
     * @param addPagination Whether to apply pagination
     * @return Response containing filtered categories with pagination info
     */
    @GetMapping("/search")
    public ResponseEntity<CategorySearchResponseDTO> searchCategories(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Integer pageNo,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) Boolean addPagination) {
        log.info("[f:searchCategories] Processing admin category search request - name: {}, isActive: {}", name, isActive);
        
        CategorySearchDTO dto = CategorySearchDTO.builder()
                .name(name)
                .isActive(isActive)
                .pageNo(pageNo)
                .pageSize(pageSize)
                .addPagination(addPagination)
                .build();
        
        return ResponseEntity.ok(categoryService.searchCategories(dto));
    }
}
