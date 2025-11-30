package com.subscription.core.controller;

import com.subscription.core.dto.ProductResponseDTO;
import com.subscription.core.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for product operations (customer endpoints).
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    /**
     * Gets all products, optionally filtered by category ID.
     *
     * @param categoryId Optional category ID to filter products
     * @return Response containing list of product response DTOs
     */
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts(
            @RequestParam(required = false) String categoryId) {
        log.info("[f:getAllProducts] Fetching products, categoryId: {}", categoryId);
        return ResponseEntity.ok(productService.getAllProducts(categoryId));
    }

    /**
     * Gets a product by ID.
     *
     * @param productId The product ID
     * @return Response containing product details
     * @throws com.subscription.core.exception.ResourceNotFoundException if product not found
     */
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable String productId) {
        log.info("[f:getProductById] Fetching product with ID: {}", productId);
        return ResponseEntity.ok(productService.getProductById(productId));
    }
}
