package com.subscription.core.dto;

import com.subscription.core.enums.CategoryStatus;
import com.subscription.core.enums.GstSlab;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * DTO for creating or updating product categories.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCategoryUpsertDTO {
    String productId;
    String discountTypeId;
    String category;
    GstSlab gstSlab;
    CategoryStatus status;
}