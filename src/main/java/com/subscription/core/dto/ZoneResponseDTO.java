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
 * Response DTO for zone details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ZoneResponseDTO {
    String zoneId;
    String zoneCode;
    String district;
    String zoneName;
    ZoneDirection direction;
    ZoneStatus status;
}
