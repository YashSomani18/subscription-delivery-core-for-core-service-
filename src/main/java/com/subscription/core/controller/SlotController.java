package com.subscription.core.controller;

import com.subscription.core.dto.SlotResponseDTO;
import com.subscription.core.dto.SlotUpsertDTO;
import com.subscription.core.service.SlotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for slot operations (customer endpoints).
 */
@RestController
@RequestMapping("/api/slots")
@RequiredArgsConstructor
@Slf4j
public class SlotController {

    private final SlotService slotService;

    /**
     * Gets all available slots.
     *
     * @return Response containing list of slots
     */
    @GetMapping
    public ResponseEntity<List<SlotResponseDTO>> getAllSlots() {
        log.info("[f:getAllSlots] Fetching all available slots");
        return ResponseEntity.ok(slotService.getAllSlots());
    }

    /**
     * Gets a slot by ID.
     *
     * @param slotId The slot ID
     * @return Response containing slot details
     */
    @GetMapping("/{slotId}")
    public ResponseEntity<SlotResponseDTO> getSlotById(@PathVariable String slotId) {
        log.info("[f:getSlotById] Fetching slot with ID: {}", slotId);
        return ResponseEntity.ok(slotService.getSlotById(slotId));
    }

    @PostMapping("/upsert")
    public ResponseEntity<String> upsert(@RequestBody SlotUpsertDTO slotRequest) {
        try {
            String message = slotService.upsertSlot(slotRequest);
            return ResponseEntity.ok(message);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error during Slot upsert operation: " + e.getMessage());
        }
    }
}
