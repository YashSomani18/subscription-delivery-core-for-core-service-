package com.subscription.core.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * DTO for searching products.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductSearchDTO {
    String query;
    String categoryId;
    Boolean isPerishable;
    Integer pageNo;
    Integer pageSize;
    Boolean addPagination;
}
