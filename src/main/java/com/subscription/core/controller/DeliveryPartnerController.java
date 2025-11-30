package com.subscription.core.controller;

import com.subscription.core.dto.DeliveryPartnerResponseDTO;
import com.subscription.core.dto.DeliveryPartnerSearchDTO;
import com.subscription.core.dto.DeliveryPartnerSearchResponseDTO;
import com.subscription.core.dto.DeliveryPartnerStatusUpdateDTO;
import com.subscription.core.dto.DeliveryPartnerUpsertDTO;
import com.subscription.core.service.DeliveryPartnerService;
import com.subscription.jwt.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for delivery partner operations (partner self-service + admin).
 */
@RestController
@RequestMapping("/api/delivery-partners")
@RequiredArgsConstructor
@Slf4j
public class DeliveryPartnerController {

    private final DeliveryPartnerService deliveryPartnerService;
    private final JwtUtil jwtUtil;

    // ==================== PARTNER SELF-SERVICE ENDPOINTS ====================

    /**
     * Gets the current delivery partner profile from JWT token.
     *
     * @param authHeader The authorization header
     * @return Response containing delivery partner profile
     */
    @GetMapping("/me")
    public ResponseEntity<DeliveryPartnerResponseDTO> getMyProfile(@RequestHeader("Authorization") String authHeader) {
        log.info("[f:getMyProfile] Fetching current delivery partner profile");
        String token = authHeader.substring(7);
        String partnerId = jwtUtil.extractUserId(token);
        return ResponseEntity.ok(deliveryPartnerService.getPartnerById(partnerId));
    }

    /**
     * Updates the current delivery partner's availability status.
     *
     * @param authHeader The authorization header
     * @param request The status update request
     * @return Response containing success message
     */
    @PutMapping("/me/status")
    public ResponseEntity<String> updateMyStatus(@RequestHeader("Authorization") String authHeader,
                                                  @Valid @RequestBody DeliveryPartnerStatusUpdateDTO request) {
        log.info("[f:updateMyStatus] Updating delivery partner status");
        String token = authHeader.substring(7);
        String partnerId = jwtUtil.extractUserId(token);
        return ResponseEntity.ok(deliveryPartnerService.updateStatus(partnerId, request.getCurrentStatus()));
    }

    /**
     * Gets a delivery partner by ID.
     *
     * @param partnerId The delivery partner ID
     * @return Response containing delivery partner profile
     */
    @GetMapping("/{partnerId}")
    public ResponseEntity<DeliveryPartnerResponseDTO> getPartnerById(@PathVariable String partnerId) {
        log.info("[f:getPartnerById] Fetching delivery partner with ID: {}", partnerId);
        return ResponseEntity.ok(deliveryPartnerService.getPartnerById(partnerId));
    }

    // ==================== ADMIN ENDPOINTS ====================

    /**
     * Admin: Creates or updates a delivery partner.
     * DevOps: Do not expose this endpoint publicly.
     *
     * @param request The delivery partner data to create or update
     * @return Response containing success message
     */
    @PostMapping("/admin/upsert")
    public ResponseEntity<String> adminUpsertDeliveryPartner(@Valid @RequestBody DeliveryPartnerUpsertDTO request) {
        log.info("[f:adminUpsertDeliveryPartner] Processing admin delivery partner upsert request");
        return ResponseEntity.ok(deliveryPartnerService.upsertDeliveryPartner(request));
    }

    /**
     * Admin: Searches delivery partners based on provided filters.
     * DevOps: Do not expose this endpoint publicly.
     *
     * @param partnerName Partner name filter
     * @param email Email filter
     * @param assignedZoneId Assigned zone ID filter
     * @param assignedWarehouseId Assigned warehouse ID filter
     * @param employmentStatus Employment status filter
     * @param currentStatus Current status filter
     * @param pageNo Page number for pagination
     * @param pageSize Page size for pagination
     * @param addPagination Whether to apply pagination
     * @return Response containing filtered delivery partners with pagination info
     */
    @GetMapping("/admin/search")
    public ResponseEntity<DeliveryPartnerSearchResponseDTO> adminSearchDeliveryPartners(
            @RequestParam(required = false) String partnerName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String assignedZoneId,
            @RequestParam(required = false) String assignedWarehouseId,
            @RequestParam(required = false) com.subscription.core.enums.EmploymentStatus employmentStatus,
            @RequestParam(required = false) com.subscription.core.enums.DeliveryPartnerCurrentStatus currentStatus,
            @RequestParam(required = false) Integer pageNo,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) Boolean addPagination) {
        log.info("[f:adminSearchDeliveryPartners] Processing admin delivery partner search request - partnerName: {}, email: {}", 
                partnerName, email);
        
        DeliveryPartnerSearchDTO dto = DeliveryPartnerSearchDTO.builder()
                .partnerName(partnerName)
                .email(email)
                .assignedZoneId(assignedZoneId)
                .assignedWarehouseId(assignedWarehouseId)
                .employmentStatus(employmentStatus)
                .currentStatus(currentStatus)
                .pageNo(pageNo)
                .pageSize(pageSize)
                .addPagination(addPagination)
                .build();
        
        return ResponseEntity.ok(deliveryPartnerService.searchDeliveryPartners(dto));
    }

    /**
     * Admin: Assigns a delivery partner to a zone.
     * DevOps: Do not expose this endpoint publicly.
     *
     * @param partnerId The delivery partner ID
     * @param zoneId The zone ID to assign
     * @return Response containing success message
     */
    @PutMapping("/admin/{partnerId}/assign-zone")
    public ResponseEntity<String> adminAssignToZone(@PathVariable String partnerId, @RequestParam String zoneId) {
        log.info("[f:adminAssignToZone] Assigning partner {} to zone {}", partnerId, zoneId);
        return ResponseEntity.ok(deliveryPartnerService.assignToZone(partnerId, zoneId));
    }

    /**
     * Admin: Assigns a delivery partner to a warehouse.
     * DevOps: Do not expose this endpoint publicly.
     *
     * @param partnerId The delivery partner ID
     * @param warehouseId The warehouse ID to assign
     * @return Response containing success message
     */
    @PutMapping("/admin/{partnerId}/assign-warehouse")
    public ResponseEntity<String> adminAssignToWarehouse(@PathVariable String partnerId, @RequestParam String warehouseId) {
        log.info("[f:adminAssignToWarehouse] Assigning partner {} to warehouse {}", partnerId, warehouseId);
        return ResponseEntity.ok(deliveryPartnerService.assignToWarehouse(partnerId, warehouseId));
    }
}
