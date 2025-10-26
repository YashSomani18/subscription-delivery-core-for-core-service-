package com.subscription.core.entity;

import com.subscription.core.enums.DiscountCategory;
import com.subscription.core.enums.DiscountStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.ZonedDateTime;

/**
 * DiscountType entity representing different types of discounts with rules and validity periods.
 */
@Entity
@Table(name = "discount_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscountType extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 64)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountCategory discountType;

    @Column(name = "rule_format", columnDefinition = "TEXT")
    private String ruleFormat;

    @Column(name = "discount_name", nullable = false)
    private String discountName;

    @Column(name = "description")
    private String description;

    @Column(name = "valid_from")
    private ZonedDateTime validFrom;

    @Column(name = "valid_until")
    private ZonedDateTime validUntil;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DiscountStatus status;
}
