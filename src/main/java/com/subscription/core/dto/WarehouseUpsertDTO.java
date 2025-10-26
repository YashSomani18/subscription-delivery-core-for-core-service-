package com.subscription.core.dto;

import com.subscription.core.enums.WarehouseStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class WarehouseUpsertDTO {
    private String warehouseName;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private Double latitude;
    private Double longitude;
    private UUID zoneId;
    private WarehouseStatus status;
}
