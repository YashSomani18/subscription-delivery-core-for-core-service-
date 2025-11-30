package com.subscription.core.dto;

import com.subscription.core.enums.ProductStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

/**
 * Response DTO for product details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponseDTO {
    String productId;
    String productName;
    BigDecimal basePrice;
    String description;
    String imageUrl;
    String sku;
    String categoryId;
    String brand;
    String unit;
    Boolean isSubscriptionEligible;
    Boolean isPerishable;
    ProductStatus status;
}
