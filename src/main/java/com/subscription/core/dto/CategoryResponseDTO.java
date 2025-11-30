package com.subscription.core.dto;

import com.subscription.core.enums.GstSlab;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Response DTO for category details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryResponseDTO {
    String categoryId;
    String name;
    String description;
    String imageUrl;
    GstSlab gstSlab;
    Integer displayOrder;
    Boolean isActive;
}

