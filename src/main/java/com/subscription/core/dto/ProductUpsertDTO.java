package com.subscription.core.dto;

import com.subscription.core.enums.ProductStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductUpsertDTO {
    private String productName;
    private BigDecimal basePrice;
    private String description;
    private String imageUrl;
    private String sku;
    private ProductStatus status;
}
