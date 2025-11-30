package com.subscription.core.dto;

import com.subscription.core.enums.DeliveryPartnerCurrentStatus;
import com.subscription.core.enums.EmploymentStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * DTO for searching delivery partners with various filters.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeliveryPartnerSearchDTO {
    String partnerName;
    String email;
    String assignedZoneId;
    String assignedWarehouseId;
    EmploymentStatus employmentStatus;
    DeliveryPartnerCurrentStatus currentStatus;
    Integer pageNo;
    Integer pageSize;
    Boolean addPagination;
}
