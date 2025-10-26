package com.subscription.core.dto;

import com.subscription.core.enums.CategoryStatus;
import com.subscription.core.enums.GstSlab;
import lombok.Data;

import java.util.UUID;

@Data
public class ProductCategoryUpsertDTO {
    private UUID productId;
    private UUID discountTypeId;
    private String category;
    private GstSlab gstSlab;
    private CategoryStatus status;
}
