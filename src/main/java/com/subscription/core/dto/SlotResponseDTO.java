package com.subscription.core.dto;

import com.subscription.core.enums.SlotStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Response DTO for slot details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SlotResponseDTO {
    String slotId;
    LocalTime startTime;
    LocalTime endTime;
    LocalDate slotDate;
    Integer capacity;
    Integer currentBookings;
    String zoneId;
    SlotStatus status;
}
