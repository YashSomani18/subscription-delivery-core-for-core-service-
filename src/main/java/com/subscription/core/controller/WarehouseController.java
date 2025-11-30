package com.subscription.core.controller;

import com.subscription.core.dto.WarehouseResponseDTO;
import com.subscription.core.dto.WarehouseSearchDTO;
import com.subscription.core.dto.WarehouseSearchResponseDTO;
import com.subscription.core.dto.WarehouseUpsertDTO;
import com.subscription.core.enums.WarehouseStatus;
import com.subscription.core.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
@Slf4j
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping("/{warehouseId}")
    public ResponseEntity<WarehouseResponseDTO> getWarehouseById(@PathVariable String warehouseId) {
        log.info("[f:getWarehouseById] Fetching warehouse with ID: {}", warehouseId);
        return new ResponseEntity<>(warehouseService.getWarehouseById(warehouseId), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<WarehouseSearchResponseDTO> searchWarehouses(
            @RequestParam(required = false) String warehouseName,
            @RequestParam(required = false) String warehouseCode,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) WarehouseStatus status,
            @RequestParam(required = false) Integer pageNo,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) Boolean addPagination) {
        
        log.info("[f:searchWarehouses] Searching warehouses - name: {}, code: {}, city: {}, state: {}", 
                warehouseName, warehouseCode, city, state);

        WarehouseSearchDTO searchDTO = WarehouseSearchDTO.builder()
                .warehouseName(warehouseName)
                .warehouseCode(warehouseCode)
                .city(city)
                .state(state)
                .status(status)
                .pageNo(pageNo)
                .pageSize(pageSize)
                .addPagination(addPagination)
                .build();

        return new ResponseEntity<>(warehouseService.searchWarehouses(searchDTO), HttpStatus.OK);
    }

    @PostMapping("/admin/upsert")
    public ResponseEntity<String> adminUpsertWarehouse(@Valid @RequestBody WarehouseUpsertDTO warehouseRequest) {
        log.info("[f:adminUpsertWarehouse] Processing admin warehouse upsert request");
        return new ResponseEntity<>(warehouseService.upsertWarehouse(warehouseRequest), HttpStatus.OK);
    }
}
