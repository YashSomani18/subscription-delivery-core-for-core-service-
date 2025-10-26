package com.subscription.core.controller;

import com.subscription.core.dto.WarehouseUpsertDTO;
import com.subscription.core.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for Warehouse entity operations
 */
@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {

    @Autowired
    private WarehouseService warehouseService;

    @PostMapping("/upsert")
    public ResponseEntity<String> upsert(@RequestBody WarehouseUpsertDTO warehouseRequest) {
        try {
            String message = warehouseService.upsertWarehouse(warehouseRequest);
            return ResponseEntity.ok(message);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error during Warehouse upsert operation: " + e.getMessage());
        }
    }
}