package com.subscription.core.entity;

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
 * Category entity representing product categories.
 * Replaces the old ProductCategory entity.
 */
@Entity
@Table(name = "categories")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Category extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "category_id", length = 64)
    String categoryId;

    @Column(name = "name", nullable = false, unique = true, columnDefinition = "varchar(255)")
    String name;

    @Column(name = "description", columnDefinition = "varchar(255)")
    String description;

    @Column(name = "image_url", columnDefinition = "varchar(255)")
    String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "gst_slab", nullable = false)
    GstSlab gstSlab;

    @Column(name = "display_order")
    Integer displayOrder;

    @Column(name = "is_active", nullable = false, columnDefinition = "TINYINT(1)")
    Boolean isActive = true;
}
