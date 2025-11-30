package com.subscription.core.dto;

import com.subscription.core.enums.WarehouseStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WarehouseResponseDTO {
    String warehouseId;
    String warehouseName;
    String warehouseCode;
    String address;
    String city;
    String state;
    String postalCode;
    Double latitude;
    Double longitude;
    WarehouseStatus status;
}

