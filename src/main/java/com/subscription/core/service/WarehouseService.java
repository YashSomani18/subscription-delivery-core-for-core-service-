package com.subscription.core.service;

import com.subscription.core.api.SearchWarehouseApi;
import com.subscription.core.dto.WarehouseResponseDTO;
import com.subscription.core.dto.WarehouseSearchDTO;
import com.subscription.core.dto.WarehouseSearchResponseDTO;
import com.subscription.core.dto.WarehouseUpsertDTO;
import com.subscription.core.entity.Warehouse;
import com.subscription.core.exception.ResourceNotFoundException;
import com.subscription.core.repository.WarehouseRepository;
import com.subscription.core.util.LambdaUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final SearchWarehouseApi searchWarehouseApi;

    @Transactional
    public String upsertWarehouse(WarehouseUpsertDTO warehouseRequest) {
        log.info("[f:upsertWarehouse] Processing warehouse: {}", warehouseRequest.getWarehouseCode());
        
        boolean isUpdate = Optional.ofNullable(warehouseRequest.getWarehouseCode())
                .map(warehouseRepository::findByWarehouseCode)
                .orElse(Optional.empty())
                .isPresent();

        Warehouse warehouse = Optional.ofNullable(warehouseRequest.getWarehouseCode())
                .map(warehouseRepository::findByWarehouseCode)
                .orElse(Optional.empty())
                .map(existingWarehouse -> {
                    updateWarehouse(existingWarehouse, warehouseRequest);
                    return existingWarehouse;
                })
                .orElse(createWarehouse(warehouseRequest));

        warehouseRepository.save(warehouse);
        log.info("[f:upsertWarehouse] Warehouse {} successfully", isUpdate ? "updated" : "created");
        return isUpdate ? "Warehouse updated successfully" : "Warehouse created successfully";
    }

    @Transactional(readOnly = true)
    public WarehouseSearchResponseDTO searchWarehouses(WarehouseSearchDTO searchDTO) {
        log.info("[f:searchWarehouses] Searching warehouses with criteria: {}", searchDTO);
        return searchWarehouseApi.search(searchDTO);
    }

    @Transactional(readOnly = true)
    public WarehouseResponseDTO getWarehouseById(String warehouseId) {
        log.info("[f:getWarehouseById] Fetching warehouse with ID: {}", warehouseId);

        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with ID: " + warehouseId));

        return toDto(warehouse);
    }

    private Warehouse createWarehouse(WarehouseUpsertDTO dto) {
        Warehouse warehouse = new Warehouse();
        warehouse.setWarehouseName(dto.getWarehouseName());
        warehouse.setWarehouseCode(dto.getWarehouseCode());
        warehouse.setAddress(dto.getAddress());
        warehouse.setCity(dto.getCity());
        warehouse.setState(dto.getState());
        warehouse.setPostalCode(dto.getPostalCode());
        warehouse.setLatitude(dto.getLatitude());
        warehouse.setLongitude(dto.getLongitude());
        warehouse.setStatus(dto.getStatus());
        return warehouse;
    }

    private void updateWarehouse(Warehouse warehouse, WarehouseUpsertDTO warehouseRequest) {
        LambdaUtil.updateIfNotNull(warehouseRequest.getWarehouseName(), warehouse::setWarehouseName);
        LambdaUtil.updateIfNotNull(warehouseRequest.getWarehouseCode(), warehouse::setWarehouseCode);
        LambdaUtil.updateIfNotNull(warehouseRequest.getAddress(), warehouse::setAddress);
        LambdaUtil.updateIfNotNull(warehouseRequest.getCity(), warehouse::setCity);
        LambdaUtil.updateIfNotNull(warehouseRequest.getState(), warehouse::setState);
        LambdaUtil.updateIfNotNull(warehouseRequest.getPostalCode(), warehouse::setPostalCode);
        LambdaUtil.updateIfNotNull(warehouseRequest.getLatitude(), warehouse::setLatitude);
        LambdaUtil.updateIfNotNull(warehouseRequest.getLongitude(), warehouse::setLongitude);
        LambdaUtil.updateIfNotNull(warehouseRequest.getStatus(), warehouse::setStatus);
    }

    private WarehouseResponseDTO toDto(Warehouse warehouse) {
        return WarehouseResponseDTO.builder()
                .warehouseId(warehouse.getWarehouseId())
                .warehouseName(warehouse.getWarehouseName())
                .warehouseCode(warehouse.getWarehouseCode())
                .address(warehouse.getAddress())
                .city(warehouse.getCity())
                .state(warehouse.getState())
                .postalCode(warehouse.getPostalCode())
                .latitude(warehouse.getLatitude())
                .longitude(warehouse.getLongitude())
                .status(warehouse.getStatus())
                .build();
    }
}
