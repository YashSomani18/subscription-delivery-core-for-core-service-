package com.subscription.core.entity;

import com.subscription.core.enums.CategoryStatus;
import com.subscription.core.enums.GstSlab;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

/**
 * ProductCategory entity representing product categorization with GST and discount information.
 */
@Entity
@Table(name = "product_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategory extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "category_id", length = 64)
    private String categoryId;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "discount_type_id")
    private String discountTypeId;

    @Column(name = "category", nullable = false)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(name = "gst_slab", nullable = false)
    private GstSlab gstSlab;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CategoryStatus status;
}
