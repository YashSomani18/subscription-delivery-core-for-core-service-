package com.subscription.core.dto;

import com.subscription.core.enums.EmploymentStatus;
import com.subscription.core.enums.VehicleType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * DTO for creating or updating delivery partners.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeliveryPartnerUpsertDTO {
    String deliveryPartnerId;
    String partnerName;
    String email;
    String phoneNumber;
    String password;
    String assignedWarehouseId;
    String assignedZoneId;
    VehicleType vehicleType;
    String vehicleNumber;
    EmploymentStatus employmentStatus;
}
