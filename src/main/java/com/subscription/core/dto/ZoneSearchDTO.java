package com.subscription.core.dto;

import com.subscription.core.enums.ZoneStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * DTO for searching zones with various filters.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ZoneSearchDTO {
    String zoneName;
    String district;
    ZoneStatus status;
    Integer pageNo;
    Integer pageSize;
}
