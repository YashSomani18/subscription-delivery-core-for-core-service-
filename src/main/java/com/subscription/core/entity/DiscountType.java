package com.subscription.core.entity;

import com.subscription.core.enums.DiscountCategory;
import com.subscription.core.enums.DiscountStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import jakarta.persistence.*;
import java.time.ZonedDateTime;

/**
 * DiscountType entity representing different types of discounts with rules and
 * validity periods.
 */
@Entity
@Table(name = "discount_types")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiscountType extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 64)
    String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    DiscountCategory discountType;

    @Column(name = "rule_format", columnDefinition = "TEXT")
    String ruleFormat;

    @Column(name = "discount_name", nullable = false, columnDefinition = "varchar(255)")
    String discountName;

    @Column(name = "description", columnDefinition = "varchar(255)")
    String description;

    @Column(name = "valid_from", columnDefinition = "TIMESTAMP")
    ZonedDateTime validFrom;

    @Column(name = "valid_until", columnDefinition = "TIMESTAMP")
    ZonedDateTime validUntil;


    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    DiscountStatus status;
}
