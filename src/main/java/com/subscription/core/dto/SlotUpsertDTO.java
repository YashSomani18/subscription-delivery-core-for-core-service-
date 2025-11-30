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
 * DTO for creating or updating delivery slots.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SlotUpsertDTO {
    LocalTime startTime;
    LocalTime endTime;
    LocalDate slotDate;
    Integer capacity;
    Integer currentBookings;
    String zoneId;
    SlotStatus status;
}