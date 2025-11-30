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
 * DTO for creating or updating products.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductUpsertDTO {
    String productName;
    BigDecimal basePrice;
    String description;
    String imageUrl;
    String sku;
    ProductStatus status;
    String categoryId;
    String brand;
    String unit;
    String images;
    String tags;
    String discountTypeId;
    Boolean isSubscriptionEligible;
    Boolean isPerishable;
}