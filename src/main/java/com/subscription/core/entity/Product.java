package com.subscription.core.entity;

import com.subscription.core.enums.ProductStatus;
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

@Entity
@Table(name = "products")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_id", length = 64)
    String productId;

    @Column(name = "product_name", nullable = false, columnDefinition = "varchar(255)")
    String productName;

    @Column(name = "base_price", nullable = false, columnDefinition = "decimal(20,4)")
    BigDecimal basePrice;

    @Column(name = "description", columnDefinition = "text")
    String description;

    @Column(name = "image_url", columnDefinition = "varchar(500)")
    String imageUrl;

    @Column(name = "sku", unique = true, nullable = false, columnDefinition = "varchar(255)")
    String sku;

    @Column(name = "category_id", columnDefinition = "varchar(255)")
    String categoryId;

    @Column(name = "brand", columnDefinition = "varchar(255)")
    String brand;

    @Column(name = "unit", columnDefinition = "varchar(255)")
    String unit;

    @Column(name = "images", columnDefinition = "TEXT")
    String images;

    @Column(name = "tags", columnDefinition = "TEXT")
    String tags;

    @Column(name = "discount_type_id", columnDefinition = "varchar(255)")
    String discountTypeId;

    @Column(name = "is_subscription_eligible", columnDefinition = "TINYINT(1)")
    Boolean isSubscriptionEligible;

    @Column(name = "is_perishable", columnDefinition = "TINYINT(1)")
    Boolean isPerishable;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    ProductStatus status;
}
