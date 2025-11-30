package com.subscription.core.service;

import com.subscription.core.api.SearchDeliveryPartnerApi;
import com.subscription.core.dto.DeliveryPartnerResponseDTO;
import com.subscription.core.dto.DeliveryPartnerSearchDTO;
import com.subscription.core.dto.DeliveryPartnerSearchResponseDTO;
import com.subscription.core.dto.DeliveryPartnerUpsertDTO;
import com.subscription.core.entity.DeliveryPartner;
import com.subscription.core.enums.DeliveryPartnerCurrentStatus;
import com.subscription.core.repository.DeliveryPartnerRepository;
import com.subscription.core.util.LambdaUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing delivery partners.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryPartnerService {

    private final DeliveryPartnerRepository deliveryPartnerRepository;
    private final SearchDeliveryPartnerApi searchDeliveryPartnerApi;
    private final PasswordService passwordService;

    /**
     * Creates or updates a delivery partner based on the provided request.
     *
     * @param request The delivery partner data to create or update
     * @return Success message with the delivery partner ID
     * @throws IllegalArgumentException if delivery partner not found during update
     */
    @Transactional
    public String upsertDeliveryPartner(DeliveryPartnerUpsertDTO request) {
        log.info("[f:upsertDeliveryPartner] Processing delivery partner upsert: {}", request.getDeliveryPartnerId());

        DeliveryPartner deliveryPartner = getOrCreateDeliveryPartner(request);
        updateDeliveryPartnerFields(deliveryPartner, request);

        deliveryPartnerRepository.save(deliveryPartner);
        log.info("[f:upsertDeliveryPartner] Delivery partner saved with ID: {}", deliveryPartner.getDeliveryPartnerId());
        return "DeliveryPartner upserted successfully with ID: " + deliveryPartner.getDeliveryPartnerId();
    }

    /**
     * Assigns a delivery partner to a specific zone.
     *
     * @param partnerId The delivery partner ID
     * @param zoneId The zone ID to assign
     * @return Success message
     * @throws IllegalArgumentException if delivery partner not found
     */
    @Transactional
    public String assignToZone(String partnerId, String zoneId) {
        log.info("[f:assignToZone] Assigning partner {} to zone {}", partnerId, zoneId);

        DeliveryPartner deliveryPartner = deliveryPartnerRepository.findById(partnerId)
                .orElseThrow(() -> new IllegalArgumentException("DeliveryPartner not found with ID: " + partnerId));

        deliveryPartner.setAssignedZoneId(zoneId);
        deliveryPartnerRepository.save(deliveryPartner);

        log.info("[f:assignToZone] Partner {} assigned to zone {}", partnerId, zoneId);
        return "DeliveryPartner assigned to zone successfully";
    }

    /**
     * Assigns a delivery partner to a specific warehouse.
     *
     * @param partnerId The delivery partner ID
     * @param warehouseId The warehouse ID to assign
     * @return Success message
     * @throws IllegalArgumentException if delivery partner not found
     */
    @Transactional
    public String assignToWarehouse(String partnerId, String warehouseId) {
        log.info("[f:assignToWarehouse] Assigning partner {} to warehouse {}", partnerId, warehouseId);

        DeliveryPartner deliveryPartner = deliveryPartnerRepository.findById(partnerId)
                .orElseThrow(() -> new IllegalArgumentException("DeliveryPartner not found with ID: " + partnerId));

        deliveryPartner.setAssignedWarehouseId(warehouseId);
        deliveryPartnerRepository.save(deliveryPartner);

        log.info("[f:assignToWarehouse] Partner {} assigned to warehouse {}", partnerId, warehouseId);
        return "DeliveryPartner assigned to warehouse successfully";
    }

    /**
     * Updates the current status of a delivery partner.
     *
     * @param partnerId The delivery partner ID
     * @param status The new status
     * @return Success message
     * @throws IllegalArgumentException if delivery partner not found
     */
    @Transactional
    public String updateStatus(String partnerId, DeliveryPartnerCurrentStatus status) {
        log.info("[f:updateStatus] Updating partner {} status to {}", partnerId, status);

        DeliveryPartner deliveryPartner = deliveryPartnerRepository.findById(partnerId)
                .orElseThrow(() -> new IllegalArgumentException("DeliveryPartner not found with ID: " + partnerId));

        deliveryPartner.setCurrentStatus(status);
        deliveryPartnerRepository.save(deliveryPartner);

        log.info("[f:updateStatus] Partner {} status updated to {}", partnerId, status);
        return "DeliveryPartner status updated successfully";
    }

    /**
     * Gets a delivery partner by ID.
     *
     * @param partnerId The delivery partner ID
     * @return Delivery partner response DTO
     * @throws IllegalArgumentException if delivery partner not found
     */
    @Transactional(readOnly = true)
    public DeliveryPartnerResponseDTO getPartnerById(String partnerId) {
        log.info("[f:getPartnerById] Fetching partner with ID: {}", partnerId);

        DeliveryPartner deliveryPartner = deliveryPartnerRepository.findById(partnerId)
                .orElseThrow(() -> new IllegalArgumentException("DeliveryPartner not found with ID: " + partnerId));

        return toDto(deliveryPartner);
    }

    /**
     * Gets all delivery partners in a specific zone.
     *
     * @param zoneId The zone ID
     * @return List of delivery partner response DTOs
     */
    @Transactional(readOnly = true)
    public List<DeliveryPartnerResponseDTO> getPartnersByZone(String zoneId) {
        log.info("[f:getPartnersByZone] Fetching partners in zone: {}", zoneId);

        List<DeliveryPartner> deliveryPartners = deliveryPartnerRepository.findByAssignedZoneId(zoneId);
        return deliveryPartners.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Searches delivery partners using the search API.
     *
     * @param searchDTO The search criteria
     * @return Search response with filtered delivery partners
     */
    @Transactional(readOnly = true)
    public DeliveryPartnerSearchResponseDTO searchDeliveryPartners(DeliveryPartnerSearchDTO searchDTO) {
        log.info("[f:searchDeliveryPartners] Searching delivery partners with criteria: {}", searchDTO);
        return searchDeliveryPartnerApi.search(searchDTO);
    }

    /**
     * Retrieves existing delivery partner or creates a new one.
     *
     * @param request The upsert request
     * @return Delivery partner entity
     * @throws IllegalArgumentException if delivery partner not found during update
     */
    private DeliveryPartner getOrCreateDeliveryPartner(DeliveryPartnerUpsertDTO request) {
        if (StringUtils.hasText(request.getDeliveryPartnerId())) {
            return deliveryPartnerRepository.findById(request.getDeliveryPartnerId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "DeliveryPartner not found with ID: " + request.getDeliveryPartnerId()));
        }
        return new DeliveryPartner();
    }

    /**
     * Updates delivery partner fields from the request.
     *
     * @param deliveryPartner The entity to update
     * @param request The request containing new values
     */
    private void updateDeliveryPartnerFields(DeliveryPartner deliveryPartner, DeliveryPartnerUpsertDTO request) {
        LambdaUtil.updateIfNotNull(request.getPartnerName(), deliveryPartner::setPartnerName);
        LambdaUtil.updateIfNotNull(request.getEmail(), deliveryPartner::setEmail);
        LambdaUtil.updateIfNotNull(request.getPhoneNumber(), deliveryPartner::setPhoneNumber);
        LambdaUtil.updateIfNotNull(request.getAssignedWarehouseId(), deliveryPartner::setAssignedWarehouseId);
        LambdaUtil.updateIfNotNull(request.getAssignedZoneId(), deliveryPartner::setAssignedZoneId);
        LambdaUtil.updateIfNotNull(request.getVehicleType(), deliveryPartner::setVehicleType);
        LambdaUtil.updateIfNotNull(request.getVehicleNumber(), deliveryPartner::setVehicleNumber);
        LambdaUtil.updateIfNotNull(request.getEmploymentStatus(), deliveryPartner::setEmploymentStatus);

        if (StringUtils.hasText(request.getPassword())) {
            deliveryPartner.setPasswordHash(passwordService.hashPassword(request.getPassword()));
        }
    }

    /**
     * Converts DeliveryPartner entity to response DTO.
     *
     * @param deliveryPartner The delivery partner entity
     * @return Response DTO
     */
    private DeliveryPartnerResponseDTO toDto(DeliveryPartner deliveryPartner) {
        return DeliveryPartnerResponseDTO.builder()
                .deliveryPartnerId(deliveryPartner.getDeliveryPartnerId())
                .partnerName(deliveryPartner.getPartnerName())
                .email(deliveryPartner.getEmail())
                .phoneNumber(deliveryPartner.getPhoneNumber())
                .assignedWarehouseId(deliveryPartner.getAssignedWarehouseId())
                .assignedZoneId(deliveryPartner.getAssignedZoneId())
                .vehicleType(deliveryPartner.getVehicleType())
                .vehicleNumber(deliveryPartner.getVehicleNumber())
                .employmentStatus(deliveryPartner.getEmploymentStatus())
                .currentStatus(deliveryPartner.getCurrentStatus())
                .totalDeliveries(deliveryPartner.getTotalDeliveries())
                .successfulDeliveries(deliveryPartner.getSuccessfulDeliveries())
                .averageRating(deliveryPartner.getAverageRating())
                .build();
    }
}
