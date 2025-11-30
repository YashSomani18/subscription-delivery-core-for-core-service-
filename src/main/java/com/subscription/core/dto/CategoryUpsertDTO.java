package com.subscription.core.dto;

import com.subscription.core.enums.GstSlab;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * DTO for creating or updating categories.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryUpsertDTO {
    String categoryId;
    String name;
    String description;
    String imageUrl;
    GstSlab gstSlab;
    Integer displayOrder;
    Boolean isActive;
}
