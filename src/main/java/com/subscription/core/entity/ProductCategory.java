package com.subscription.core.entity;

import com.subscription.core.enums.CategoryStatus;
import com.subscription.core.enums.GstSlab;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import jakarta.persistence.*;

/**
 * ProductCategory entity representing product categorization with GST and discount information.
 */
@Entity
@Table(name = "product_categories")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCategory extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "category_id", length = 64)
    String categoryId;

    @Column(name = "product_id", nullable = false, columnDefinition = "varchar(255)")
    String productId;

    @Column(name = "discount_type_id", columnDefinition = "varchar(255)")
    String discountTypeId;

    @Column(name = "category", nullable = false, columnDefinition = "varchar(255)")
    String category;

    @Enumerated(EnumType.STRING)
    @Column(name = "gst_slab", nullable = false)
    GstSlab gstSlab;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    CategoryStatus status;
}
