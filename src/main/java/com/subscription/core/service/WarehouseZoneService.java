package com.subscription.core.service;

import com.subscription.core.dto.WarehouseZoneUpsertDTO;
import com.subscription.core.entity.WarehouseZone;
import com.subscription.core.repository.WarehouseZoneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseZoneService {

    private final WarehouseZoneRepository warehouseZoneRepository;

    public String upsertWarehouseZone(WarehouseZoneUpsertDTO dto) {
        log.info("[f:upsertWarehouseZone] Processing warehouse-zone mapping: warehouse={}, zone={}",
                dto.getWarehouseId(), dto.getZoneId());

        boolean isUpdate = warehouseZoneRepository
                .findByWarehouseIdAndZoneId(dto.getWarehouseId(), dto.getZoneId())
                .isPresent();

        WarehouseZone warehouseZone = warehouseZoneRepository
                .findByWarehouseIdAndZoneId(dto.getWarehouseId(), dto.getZoneId())
                .map(existing -> {
                    existing.setIsPrimary(dto.getIsPrimary());
                    return existing;
                })
                .orElseGet(() -> createWarehouseZone(dto));

        warehouseZoneRepository.save(warehouseZone);
        log.info("[f:upsertWarehouseZone] Warehouse-zone mapping {} successfully",
                isUpdate ? "updated" : "created");

        return isUpdate ? "Warehouse-zone mapping updated successfully" : "Warehouse-zone mapping created successfully";
    }

    private WarehouseZone createWarehouseZone(WarehouseZoneUpsertDTO dto) {
        return WarehouseZone.builder()
                .warehouseId(dto.getWarehouseId())
                .zoneId(dto.getZoneId())
                .isPrimary(dto.getIsPrimary())
                .build();
    }
}
