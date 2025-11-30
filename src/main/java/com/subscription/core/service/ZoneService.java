package com.subscription.core.service;

import com.subscription.core.dto.ZoneResponseDTO;
import com.subscription.core.dto.ZoneSearchDTO;
import com.subscription.core.dto.ZoneSearchResponseDTO;
import com.subscription.core.dto.ZoneUpsertDTO;
import com.subscription.core.entity.Zone;
import com.subscription.core.repository.ZoneRepository;
import com.subscription.core.util.LambdaUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing zones.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ZoneService {
    
    private final ZoneRepository zoneRepository;
    
    private static final int DEFAULT_PAGE_NO = 0;
    private static final int DEFAULT_PAGE_SIZE = 20;

    /**
     * Creates or updates a zone based on the provided request.
     *
     * @param zoneRequest The zone data to create or update
     * @return Success message
     */
    @Transactional
    public String upsertZone(ZoneUpsertDTO zoneRequest) {
        log.info("[f:upsertZone] Processing zone upsert for zone: {}", zoneRequest.getZoneName());
        
        boolean isUpdate = Optional.ofNullable(zoneRequest.getZoneName())
            .map(zoneRepository::findByZoneName)
            .orElse(Optional.empty())
            .isPresent();
            
        Zone zone = Optional.ofNullable(zoneRequest.getZoneName())
            .map(zoneRepository::findByZoneName)
            .orElse(Optional.empty())
            .map(existingZone -> {
                updateZone(existingZone, zoneRequest);
                return existingZone;
            })
            .orElse(createZone(zoneRequest));
        
        zoneRepository.save(zone);
        log.info("[f:upsertZone] Zone {} with ID: {}", isUpdate ? "updated" : "created", zone.getZoneId());
        return isUpdate ? "Zone updated successfully" : "Zone created successfully";
    }

    /**
     * Searches zones based on provided filters with pagination.
     *
     * @param searchDTO The search criteria
     * @return Search response with filtered zones
     */
    @Transactional(readOnly = true)
    public ZoneSearchResponseDTO searchZones(ZoneSearchDTO searchDTO) {
        log.info("[f:searchZones] Searching zones with criteria: {}", searchDTO);

        int pageNo = Objects.nonNull(searchDTO.getPageNo()) ? searchDTO.getPageNo() : DEFAULT_PAGE_NO;
        int pageSize = Objects.nonNull(searchDTO.getPageSize()) ? searchDTO.getPageSize() : DEFAULT_PAGE_SIZE;

        List<Zone> zones = getFilteredZones(searchDTO);

        int startIndex = pageNo * pageSize;
        int endIndex = Math.min(startIndex + pageSize, zones.size());

        List<Zone> paginatedZones = zones.subList(
                Math.min(startIndex, zones.size()),
                endIndex
        );

        List<ZoneResponseDTO> zoneDtos = paginatedZones.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        log.info("[f:searchZones] Found {} zones, returning page {} with {} results",
                zones.size(), pageNo, zoneDtos.size());

        return ZoneSearchResponseDTO.builder()
                .pageNumber(pageNo)
                .pageSize(pageSize)
                .totalResults(zones.size())
                .zones(zoneDtos)
                .build();
    }

    /**
     * Filters zones based on search criteria.
     *
     * @param searchDTO The search criteria
     * @return List of filtered zones
     */
    private List<Zone> getFilteredZones(ZoneSearchDTO searchDTO) {
        if (Objects.nonNull(searchDTO.getZoneName())) {
            return zoneRepository.findByZoneName(searchDTO.getZoneName())
                    .map(List::of)
                    .orElse(List.of());
        } else if (Objects.nonNull(searchDTO.getDistrict())) {
            return zoneRepository.findByDistrict(searchDTO.getDistrict());
        } else if (Objects.nonNull(searchDTO.getStatus())) {
            return zoneRepository.findByStatus(searchDTO.getStatus());
        } else {
            return zoneRepository.findAll(Sort.by("zoneName").ascending());
        }
    }

    /**
     * Creates a new zone from the request.
     *
     * @param dto The zone data
     * @return New zone entity
     */
    private Zone createZone(ZoneUpsertDTO dto) {
        Zone zone = new Zone();
        zone.setZoneCode(dto.getZoneCode());
        zone.setDistrict(dto.getDistrict());
        zone.setZoneName(dto.getZoneName());
        zone.setDirection(dto.getDirection());
        zone.setStatus(dto.getStatus());
        return zone;
    }

    /**
     * Updates an existing zone with new data.
     *
     * @param zone The existing zone
     * @param zoneRequest The new data
     */
    private void updateZone(Zone zone, ZoneUpsertDTO zoneRequest) {
        LambdaUtil.updateIfNotNull(zoneRequest.getZoneCode(), zone::setZoneCode);
        LambdaUtil.updateIfNotNull(zoneRequest.getDistrict(), zone::setDistrict);
        LambdaUtil.updateIfNotNull(zoneRequest.getZoneName(), zone::setZoneName);
        LambdaUtil.updateIfNotNull(zoneRequest.getDirection(), zone::setDirection);
        LambdaUtil.updateIfNotNull(zoneRequest.getStatus(), zone::setStatus);
    }

    /**
     * Converts Zone entity to response DTO.
     *
     * @param zone The zone entity
     * @return Response DTO
     */
    private ZoneResponseDTO toDto(Zone zone) {
        return ZoneResponseDTO.builder()
                .zoneId(zone.getZoneId())
                .zoneCode(zone.getZoneCode())
                .district(zone.getDistrict())
                .zoneName(zone.getZoneName())
                .direction(zone.getDirection())
                .status(zone.getStatus())
                .build();
    }
}