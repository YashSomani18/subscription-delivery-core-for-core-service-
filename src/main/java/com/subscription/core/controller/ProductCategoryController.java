package com.subscription.core.controller;

import com.subscription.core.dto.ProductCategoryUpsertDTO;
import com.subscription.core.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for ProductCategory entity operations
 */
@RestController
@RequestMapping("/api/product-categories")
public class ProductCategoryController {

    @Autowired
    private ProductCategoryService productCategoryService;

    @PostMapping("/upsert")
    public ResponseEntity<String> upsert(@RequestBody ProductCategoryUpsertDTO productCategoryRequest) {
        try {
            String message = productCategoryService.upsertProductCategory(productCategoryRequest);
            return ResponseEntity.ok(message);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error during ProductCategory upsert operation: " + e.getMessage());
        }
    }
}