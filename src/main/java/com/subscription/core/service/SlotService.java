package com.subscription.core.service;

import com.subscription.core.dto.SlotResponseDTO;
import com.subscription.core.dto.SlotSearchDTO;
import com.subscription.core.dto.SlotSearchResponseDTO;
import com.subscription.core.dto.SlotUpsertDTO;
import com.subscription.core.entity.Slot;
import com.subscription.core.repository.SlotRepository;
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
 * Service for managing slots.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SlotService {
    
    private final SlotRepository slotRepository;
    
    private static final int DEFAULT_PAGE_NO = 0;
    private static final int DEFAULT_PAGE_SIZE = 20;

    /**
     * Creates or updates a slot based on the provided request.
     *
     * @param slotRequest The slot data to create or update
     * @return Success message
     */
    @Transactional
    public String upsertSlot(SlotUpsertDTO slotRequest) {
        log.info("[f:upsertSlot] Processing slot upsert for zone: {}", slotRequest.getZoneId());
        
        boolean isUpdate = Optional.ofNullable(slotRequest.getZoneId())
            .map(zoneId -> slotRepository.findByZoneIdAndSlotDateAndStartTimeAndEndTime(
                zoneId,
                slotRequest.getSlotDate(),
                slotRequest.getStartTime(),
                slotRequest.getEndTime()
            ))
            .orElse(Optional.empty())
            .isPresent();
            
        Slot slot = Optional.ofNullable(slotRequest.getZoneId())
            .map(zoneId -> slotRepository.findByZoneIdAndSlotDateAndStartTimeAndEndTime(
                zoneId,
                slotRequest.getSlotDate(),
                slotRequest.getStartTime(),
                slotRequest.getEndTime()
            ))
            .orElse(Optional.empty())
            .map(existingSlot -> {
                updateSlot(existingSlot, slotRequest);
                return existingSlot;
            })
            .orElse(createSlot(slotRequest));
        
        slotRepository.save(slot);
        log.info("[f:upsertSlot] Slot {} with ID: {}", isUpdate ? "updated" : "created", slot.getSlotId());
        return isUpdate ? "Slot updated successfully" : "Slot created successfully";
    }

    /**
     * Searches slots based on provided filters with pagination.
     *
     * @param searchDTO The search criteria
     * @return Search response with filtered slots
     */
    @Transactional(readOnly = true)
    public SlotSearchResponseDTO searchSlots(SlotSearchDTO searchDTO) {
        log.info("[f:searchSlots] Searching slots with criteria: {}", searchDTO);

        int pageNo = Objects.nonNull(searchDTO.getPageNo()) ? searchDTO.getPageNo() : DEFAULT_PAGE_NO;
        int pageSize = Objects.nonNull(searchDTO.getPageSize()) ? searchDTO.getPageSize() : DEFAULT_PAGE_SIZE;

        List<Slot> slots = getFilteredSlots(searchDTO);

        int startIndex = pageNo * pageSize;
        int endIndex = Math.min(startIndex + pageSize, slots.size());

        List<Slot> paginatedSlots = slots.subList(
                Math.min(startIndex, slots.size()),
                endIndex
        );

        List<SlotResponseDTO> slotDtos = paginatedSlots.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        log.info("[f:searchSlots] Found {} slots, returning page {} with {} results",
                slots.size(), pageNo, slotDtos.size());

        return SlotSearchResponseDTO.builder()
                .pageNumber(pageNo)
                .pageSize(pageSize)
                .totalResults(slots.size())
                .slots(slotDtos)
                .build();
    }

    /**
     * Filters slots based on search criteria.
     *
     * @param searchDTO The search criteria
     * @return List of filtered slots
     */
    private List<Slot> getFilteredSlots(SlotSearchDTO searchDTO) {
        if (Objects.nonNull(searchDTO.getZoneId())) {
            return slotRepository.findByZoneId(searchDTO.getZoneId());
        } else if (Objects.nonNull(searchDTO.getSlotDate())) {
            return slotRepository.findBySlotDate(searchDTO.getSlotDate());
        } else if (Objects.nonNull(searchDTO.getStatus())) {
            return slotRepository.findByStatus(searchDTO.getStatus());
        } else {
            return slotRepository.findAll(Sort.by("slotDate").descending().and(Sort.by("startTime")));
        }
    }

    /**
     * Creates a new slot from the request.
     *
     * @param dto The slot data
     * @return New slot entity
     */
    private Slot createSlot(SlotUpsertDTO dto) {
        Slot slot = new Slot();
        slot.setStartTime(dto.getStartTime());
        slot.setEndTime(dto.getEndTime());
        slot.setSlotDate(dto.getSlotDate());
        slot.setCapacity(dto.getCapacity());
        slot.setCurrentBookings(dto.getCurrentBookings());
        slot.setZoneId(dto.getZoneId());
        slot.setStatus(dto.getStatus());
        return slot;
    }

    /**
     * Updates an existing slot with new data.
     *
     * @param slot The existing slot
     * @param slotRequest The new data
     */
    private void updateSlot(Slot slot, SlotUpsertDTO slotRequest) {
        LambdaUtil.updateIfNotNull(slotRequest.getStartTime(), slot::setStartTime);
        LambdaUtil.updateIfNotNull(slotRequest.getEndTime(), slot::setEndTime);
        LambdaUtil.updateIfNotNull(slotRequest.getSlotDate(), slot::setSlotDate);
        LambdaUtil.updateIfNotNull(slotRequest.getCapacity(), slot::setCapacity);
        LambdaUtil.updateIfNotNull(slotRequest.getCurrentBookings(), slot::setCurrentBookings);
        LambdaUtil.updateIfNotNull(slotRequest.getZoneId(), slot::setZoneId);
        LambdaUtil.updateIfNotNull(slotRequest.getStatus(), slot::setStatus);
    }

    /**
     * Gets all available slots.
     *
     * @return List of available slot response DTOs
     */
    @Transactional(readOnly = true)
    public List<SlotResponseDTO> getAllSlots() {
        log.info("[f:getAllSlots] Fetching all available slots");

        List<Slot> slots = slotRepository.findAll(Sort.by("slotDate").ascending().and(Sort.by("startTime")));
        return slots.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Gets a slot by ID.
     *
     * @param slotId The slot ID
     * @return Slot response DTO
     * @throws IllegalArgumentException if slot not found
     */
    @Transactional(readOnly = true)
    public SlotResponseDTO getSlotById(String slotId) {
        log.info("[f:getSlotById] Fetching slot: {}", slotId);

        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Slot not found with ID: " + slotId));

        return toDto(slot);
    }

    /**
     * Converts Slot entity to response DTO.
     *
     * @param slot The slot entity
     * @return Response DTO
     */
    private SlotResponseDTO toDto(Slot slot) {
        return SlotResponseDTO.builder()
                .slotId(slot.getSlotId())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .slotDate(slot.getSlotDate())
                .capacity(slot.getCapacity())
                .currentBookings(slot.getCurrentBookings())
                .zoneId(slot.getZoneId())
                .status(slot.getStatus())
                .build();
    }
}