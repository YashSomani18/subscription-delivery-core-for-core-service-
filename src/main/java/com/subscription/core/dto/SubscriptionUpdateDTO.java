package com.subscription.core.dto;

import com.subscription.core.enums.SubscriptionFrequency;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * DTO for updating subscription slot, products, or frequency.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscriptionUpdateDTO {
    String slotId;
    String productIds;
    SubscriptionFrequency frequency;
}
