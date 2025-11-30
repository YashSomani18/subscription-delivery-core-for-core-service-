package com.subscription.core.dto;

import com.subscription.core.enums.ZoneDirection;
import com.subscription.core.enums.ZoneStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * DTO for creating or updating zones.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ZoneUpsertDTO {
    String zoneCode;
    String district;
    String zoneName;
    ZoneDirection direction;
    ZoneStatus status;
}