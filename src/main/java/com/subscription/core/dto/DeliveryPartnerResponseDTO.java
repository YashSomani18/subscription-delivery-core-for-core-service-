package com.subscription.core.dto;

import com.subscription.core.enums.DeliveryPartnerCurrentStatus;
import com.subscription.core.enums.EmploymentStatus;
import com.subscription.core.enums.VehicleType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

/**
 * Response DTO for delivery partner details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeliveryPartnerResponseDTO {
    String deliveryPartnerId;
    String partnerName;
    String email;
    String phoneNumber;
    String assignedWarehouseId;
    String assignedZoneId;
    VehicleType vehicleType;
    String vehicleNumber;
    EmploymentStatus employmentStatus;
    DeliveryPartnerCurrentStatus currentStatus;
    Integer totalDeliveries;
    Integer successfulDeliveries;
    BigDecimal averageRating;
}
