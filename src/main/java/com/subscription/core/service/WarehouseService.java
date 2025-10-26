package com.subscription.core.service;

import com.subscription.core.dto.WarehouseUpsertDTO;
import com.subscription.core.entity.Warehouse;
import com.subscription.core.repository.WarehouseRepository;
import com.subscription.core.util.LambdaUtil;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    public WarehouseService(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    public String upsertWarehouse(WarehouseUpsertDTO warehouseRequest) {
        boolean isUpdate = Optional.ofNullable(warehouseRequest.getWarehouseName())
            .map(warehouseRepository::findByWarehouseName)
            .orElse(Optional.empty())
            .isPresent();
            
        Warehouse warehouse = Optional.ofNullable(warehouseRequest.getWarehouseName())
            .map(warehouseRepository::findByWarehouseName)
            .orElse(Optional.empty())
            .map(existingWarehouse -> {
                updateWarehouse(existingWarehouse, warehouseRequest);
                return existingWarehouse;
            })
            .orElse(createWarehouse(warehouseRequest));
        
        warehouseRepository.save(warehouse);
        return isUpdate ? "Warehouse updated successfully" : "Warehouse created successfully";
    }

    private Warehouse createWarehouse(WarehouseUpsertDTO dto) {
        Warehouse warehouse = new Warehouse();
        warehouse.setWarehouseName(dto.getWarehouseName());
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
        LambdaUtil.updateIfNotNull(warehouseRequest.getAddress(), warehouse::setAddress);
        LambdaUtil.updateIfNotNull(warehouseRequest.getCity(), warehouse::setCity);
        LambdaUtil.updateIfNotNull(warehouseRequest.getState(), warehouse::setState);
        LambdaUtil.updateIfNotNull(warehouseRequest.getPostalCode(), warehouse::setPostalCode);
        LambdaUtil.updateIfNotNull(warehouseRequest.getLatitude(), warehouse::setLatitude);
        LambdaUtil.updateIfNotNull(warehouseRequest.getLongitude(), warehouse::setLongitude);
        LambdaUtil.updateIfNotNull(warehouseRequest.getStatus(), warehouse::setStatus);
    }
}
