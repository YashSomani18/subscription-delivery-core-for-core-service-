package com.subscription.core.entity;

import com.subscription.core.enums.SubscriptionFrequency;
import com.subscription.core.enums.SubscriptionStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * Subscription entity representing user subscriptions with delivery scheduling.
 */
@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Subscription extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "subscription_id", length = 64)
    String subscriptionId;

    @Column(name = "user_id", nullable = false, columnDefinition = "varchar(255)")
    String userId;

    @Column(name = "slot_id", nullable = false, columnDefinition = "varchar(255)")
    String slotId;

    @Column(name = "delivery_address_id", columnDefinition = "varchar(255)")
    String deliveryAddressId;

    @Column(name = "product_ids", columnDefinition = "TEXT")
    String productIds;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency", nullable = false)
    SubscriptionFrequency frequency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    SubscriptionStatus status;

    @Column(name = "start_date", nullable = false, columnDefinition = "TIMESTAMP")
    ZonedDateTime startDate;

    @Column(name = "next_delivery_date", nullable = false, columnDefinition = "TIMESTAMP")
    ZonedDateTime nextDeliveryDate;

    @Column(name = "total_amount", nullable = false, columnDefinition = "decimal(20,4)")
    BigDecimal totalAmount;

    @Column(name = "deliveries_completed")
    Integer deliveriesCompleted;

    @Column(name = "deliveries_failed")
    Integer deliveriesFailed;

    @Column(name = "paused_at", columnDefinition = "TIMESTAMP")
    ZonedDateTime pausedAt;

    @Column(name = "pause_reason", columnDefinition = "varchar(255)")
    String pauseReason;
}

