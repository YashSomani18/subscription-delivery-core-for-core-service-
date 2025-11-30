package com.subscription.core.dto;

import com.subscription.core.enums.SubscriptionFrequency;
import com.subscription.core.enums.SubscriptionStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * DTO for searching subscriptions with various filters.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscriptionSearchDTO {
    String userId;
    String slotId;
    SubscriptionStatus status;
    SubscriptionFrequency frequency;
    Integer pageNo;
    Integer pageSize;
    Boolean addPagination;
}
