package com.subscription.core.service;

import com.subscription.core.api.SearchProductApi;
import com.subscription.core.dto.ProductResponseDTO;
import com.subscription.core.dto.ProductSearchDTO;
import com.subscription.core.dto.ProductSearchResponseDTO;
import com.subscription.core.dto.ProductUpsertDTO;
import com.subscription.core.exception.ResourceNotFoundException;
import com.subscription.shared.dto.event.ProductUpdatedEvent;
import com.subscription.core.entity.Product;
import com.subscription.core.repository.ProductRepository;
import com.subscription.core.util.LambdaUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final EntityMapper entityMapper;
    private final OutboxEventPublisher outboxEventPublisher;
    private final SearchProductApi searchProductApi;

    /**
     * Creates or updates a product based on the provided request.
     *
     * @param productRequest The product data to create or update
     * @return Success message indicating if product was created or updated
     */
    @CacheEvict(value = "productSearch", allEntries = true)
    @Transactional
    public String upsertProduct(ProductUpsertDTO productRequest) {
        log.info("[f:upsertProduct] Processing product upsert: {}", productRequest.getSku());
        
        boolean isUpdate = checkIfProductExists(productRequest.getSku());
        Product product = getOrCreateProduct(productRequest, isUpdate);
        Product savedProduct = productRepository.save(product);
        
        publishProductEvent(savedProduct, isUpdate);
        
        log.info("[f:upsertProduct] Product {} successfully with ID: {}", 
                isUpdate ? "updated" : "created", savedProduct.getProductId());
        return isUpdate ? "Product updated successfully" : "Product created successfully";
    }

    /**
     * Checks if a product exists by SKU.
     *
     * @param sku The product SKU
     * @return true if product exists, false otherwise
     */
    private boolean checkIfProductExists(String sku) {
        return Optional.ofNullable(sku)
                .map(productRepository::findBySku)
                .orElse(Optional.empty())
                .isPresent();
    }

    /**
     * Retrieves existing product or creates a new one.
     *
     * @param productRequest The upsert request
     * @param isUpdate Whether this is an update operation
     * @return Product entity
     */
    private Product getOrCreateProduct(ProductUpsertDTO productRequest, boolean isUpdate) {
        if (isUpdate) {
            Product existing = productRepository.findBySku(productRequest.getSku())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found with SKU: " + productRequest.getSku()));
            updateProduct(existing, productRequest);
            return existing;
        }
        return createProduct(productRequest);
    }

    /**
     * Publishes product update event to outbox.
     *
     * @param product The saved product
     * @param isUpdate Whether this was an update operation
     */
    private void publishProductEvent(Product product, boolean isUpdate) {
        outboxEventPublisher.publish(
                "ProductUpdated",
                "Product",
                product.getProductId(),
                ProductUpdatedEvent.builder()
                        .productId(product.getProductId())
                        .productName(product.getProductName())
                        .sku(product.getSku())
                        .updateType(isUpdate ? "UPDATE" : "CREATE")
                        .updatedAt(ZonedDateTime.now())
                        .build());
    }

    /**
     * Searches products using the search API.
     *
     * @param searchDTO The search criteria
     * @return Search response with filtered products
     */
    public ProductSearchResponseDTO searchProducts(ProductSearchDTO searchDTO) {
        log.info("[f:searchProducts] Searching products with criteria: {}", searchDTO);
        return searchProductApi.search(searchDTO);
    }

    /**
     * Gets all products, optionally filtered by category ID.
     *
     * @param categoryId Optional category ID to filter products
     * @return List of product response DTOs
     */
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAllProducts(String categoryId) {
        log.info("[f:getAllProducts] Fetching products, categoryId: {}", categoryId);

        List<Product> products = Objects.nonNull(categoryId)
                ? productRepository.findByCategoryId(categoryId)
                : productRepository.findAll();

        return products.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Gets a product by ID.
     *
     * @param productId The product ID
     * @return Product response DTO
     * @throws ResourceNotFoundException if product not found
     */
    @Transactional(readOnly = true)
    public ProductResponseDTO getProductById(String productId) {
        log.info("[f:getProductById] Fetching product with ID: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        return toDto(product);
    }

    /**
     * Creates a new product from DTO.
     *
     * @param dto The product DTO
     * @return New product entity
     */
    private Product createProduct(ProductUpsertDTO dto) {
        return Product.builder()
                .productName(dto.getProductName())
                .basePrice(dto.getBasePrice())
                .description(dto.getDescription())
                .imageUrl(dto.getImageUrl())
                .sku(dto.getSku())
                .status(dto.getStatus())
                .categoryId(dto.getCategoryId())
                .brand(dto.getBrand())
                .unit(dto.getUnit())
                .images(dto.getImages())
                .tags(dto.getTags())
                .discountTypeId(dto.getDiscountTypeId())
                .isSubscriptionEligible(dto.getIsSubscriptionEligible())
                .isPerishable(dto.getIsPerishable())
                .build();
    }

    /**
     * Updates product fields from request.
     *
     * @param product The product to update
     * @param productRequest The request containing new values
     */
    private void updateProduct(Product product, ProductUpsertDTO productRequest) {
        LambdaUtil.updateIfNotNull(productRequest.getProductName(), product::setProductName);
        LambdaUtil.updateIfNotNull(productRequest.getBasePrice(), product::setBasePrice);
        LambdaUtil.updateIfNotNull(productRequest.getDescription(), product::setDescription);
        LambdaUtil.updateIfNotNull(productRequest.getImageUrl(), product::setImageUrl);
        LambdaUtil.updateIfNotNull(productRequest.getSku(), product::setSku);
        LambdaUtil.updateIfNotNull(productRequest.getStatus(), product::setStatus);
        LambdaUtil.updateIfNotNull(productRequest.getCategoryId(), product::setCategoryId);
        LambdaUtil.updateIfNotNull(productRequest.getBrand(), product::setBrand);
        LambdaUtil.updateIfNotNull(productRequest.getUnit(), product::setUnit);
        LambdaUtil.updateIfNotNull(productRequest.getImages(), product::setImages);
        LambdaUtil.updateIfNotNull(productRequest.getTags(), product::setTags);
        LambdaUtil.updateIfNotNull(productRequest.getDiscountTypeId(), product::setDiscountTypeId);
        LambdaUtil.updateIfNotNull(productRequest.getIsSubscriptionEligible(), product::setIsSubscriptionEligible);
        LambdaUtil.updateIfNotNull(productRequest.getIsPerishable(), product::setIsPerishable);
    }

    /**
     * Converts Product entity to response DTO.
     *
     * @param product The product entity
     * @return Response DTO
     */
    private ProductResponseDTO toDto(Product product) {
        return ProductResponseDTO.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .basePrice(product.getBasePrice())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .sku(product.getSku())
                .categoryId(product.getCategoryId())
                .brand(product.getBrand())
                .unit(product.getUnit())
                .isSubscriptionEligible(product.getIsSubscriptionEligible())
                .isPerishable(product.getIsPerishable())
                .status(product.getStatus())
                .build();
    }
}