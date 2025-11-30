package com.subscription.core.service;

import com.subscription.core.dto.ProductResponseDTO;
import com.subscription.core.entity.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EntityMapper {

    public ProductResponseDTO toProductResponseDTO(Product product) {
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

    public List<ProductResponseDTO> toProductResponseDTOList(List<Product> products) {
        return products.stream()
                .map(this::toProductResponseDTO)
                .collect(Collectors.toList());
    }
}
