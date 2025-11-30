package com.subscription.core.dto;

import com.subscription.core.enums.SubscriptionFrequency;
import com.subscription.core.enums.SubscriptionStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * DTO for creating or updating subscriptions.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscriptionUpsertDTO {
    String userId;
    String slotId;
    String deliveryAddressId;
    String productIds;
    SubscriptionFrequency frequency;
    SubscriptionStatus status;
    ZonedDateTime startDate;
    ZonedDateTime nextDeliveryDate;
    BigDecimal totalAmount;
    Integer deliveriesCompleted;
    Integer deliveriesFailed;
    ZonedDateTime pausedAt;
    String pauseReason;
}