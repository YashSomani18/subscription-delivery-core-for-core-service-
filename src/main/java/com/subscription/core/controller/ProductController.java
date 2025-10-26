package com.subscription.core.controller;

import com.subscription.core.dto.ProductUpsertDTO;
import com.subscription.core.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for Product entity operations
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/upsert")
    public ResponseEntity<String> upsert(@RequestBody ProductUpsertDTO productRequest) {
        try {
            String message = productService.upsertProduct(productRequest);
            return ResponseEntity.ok(message);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error during Product upsert operation: " + e.getMessage());
        }
    }
}
