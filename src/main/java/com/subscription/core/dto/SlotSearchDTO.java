package com.subscription.core.dto;

import com.subscription.core.enums.SlotStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

/**
 * DTO for searching slots with various filters.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SlotSearchDTO {
    String zoneId;
    LocalDate slotDate;
    SlotStatus status;
    Integer pageNo;
    Integer pageSize;
}
