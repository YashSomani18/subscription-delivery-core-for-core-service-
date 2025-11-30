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
public class WarehouseSearchDTO {
    String warehouseName;
    String warehouseCode;
    String city;
    String state;
    WarehouseStatus status;
    Integer pageNo;
    Integer pageSize;
    Boolean addPagination;
}

