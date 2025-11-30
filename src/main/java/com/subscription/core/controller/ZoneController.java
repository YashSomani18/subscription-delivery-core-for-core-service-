package com.subscription.core.controller;

import com.subscription.core.dto.ZoneUpsertDTO;
import com.subscription.core.service.ZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/zones")
public class ZoneController {

    @Autowired
    private ZoneService zoneService;

    @PostMapping("/upsert")
    public ResponseEntity<String> upsert(@RequestBody ZoneUpsertDTO zoneRequest) {
        try {
            String message = zoneService.upsertZone(zoneRequest);
            return ResponseEntity.ok(message);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error during Zone upsert operation: " + e.getMessage());
        }
    }
}
