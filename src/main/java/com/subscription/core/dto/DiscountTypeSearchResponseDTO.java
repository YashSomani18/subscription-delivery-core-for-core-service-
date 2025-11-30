package com.subscription.core.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * Response DTO for discount type search results with pagination information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiscountTypeSearchResponseDTO {
    Integer pageNumber;
    Integer pageSize;
    Integer totalResults;
    List<DiscountTypeResponseDTO> discountTypes;
}

