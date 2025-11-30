package com.subscription.core.dto;

import com.subscription.core.enums.DiscountCategory;
import com.subscription.core.enums.DiscountStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * DTO for creating or updating discount types.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiscountTypeUpsertDTO {
    String id;
    DiscountCategory discountType;
    String ruleFormat;
    String discountName;
    String description;
    ZonedDateTime validFrom;
    ZonedDateTime validUntil;
    DiscountStatus status;
}
