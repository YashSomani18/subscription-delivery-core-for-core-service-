package com.subscription.core.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionCreatedEvent {
    private String subscriptionId;
    private String userId;
    private String products;
    private ZonedDateTime deliveryDate;
    private BigDecimal totalAmount;
    private ZonedDateTime createdAt;
}
