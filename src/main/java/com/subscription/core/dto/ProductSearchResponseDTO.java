package com.subscription.core.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * Response DTO for product search results with pagination information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductSearchResponseDTO {
    List<ProductResponseDTO> products;
    Long totalCount;
    Integer pageNumber;
    Integer pageSize;
}
