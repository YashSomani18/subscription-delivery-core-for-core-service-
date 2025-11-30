package com.subscription.core.dto;

import com.subscription.core.enums.DiscountCategory;
import com.subscription.core.enums.DiscountStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * DTO for searching discount types with various filters.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiscountTypeSearchDTO {
    String discountName;
    DiscountCategory discountType;
    DiscountStatus status;
    Integer pageNo;
    Integer pageSize;
    Boolean addPagination;
}
