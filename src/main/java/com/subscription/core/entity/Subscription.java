package com.subscription.core.entity;

import com.subscription.core.enums.SubscriptionFrequency;
import com.subscription.core.enums.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "subscriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subscription extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "subscription_id", length = 64)
    private String subscriptionId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "slot_id", nullable = false)
    private String slotId;

    @Column(name = "product_ids", columnDefinition = "TEXT")
    private String productIds;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency", nullable = false)
    private SubscriptionFrequency frequency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SubscriptionStatus status;

    @Column(name = "start_date", nullable = false)
    private ZonedDateTime startDate;

    @Column(name = "next_delivery_date", nullable = false)
    private ZonedDateTime nextDeliveryDate;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "deliveries_completed")
    private Integer deliveriesCompleted;

    @Column(name = "deliveries_failed")
    private Integer deliveriesFailed;

    @Column(name = "paused_at")
    private ZonedDateTime pausedAt;

    @Column(name = "pause_reason")
    private String pauseReason;
}

