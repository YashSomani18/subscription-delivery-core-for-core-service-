package com.subscription.core.dto;

import com.subscription.core.enums.DeliveryPartnerCurrentStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * DTO for updating delivery partner availability status.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeliveryPartnerStatusUpdateDTO {
    DeliveryPartnerCurrentStatus currentStatus;
}
