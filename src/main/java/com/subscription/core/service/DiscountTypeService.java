package com.subscription.core.service;

import com.subscription.core.api.SearchDiscountTypeApi;
import com.subscription.core.dto.DiscountTypeSearchDTO;
import com.subscription.core.dto.DiscountTypeSearchResponseDTO;
import com.subscription.core.dto.DiscountTypeUpsertDTO;
import com.subscription.core.entity.DiscountType;
import com.subscription.core.repository.DiscountTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiscountTypeService {

    private final DiscountTypeRepository discountTypeRepository;
    private final SearchDiscountTypeApi searchDiscountTypeApi;

    /**
     * Creates or updates a discount type based on the provided request.
     *
     * @param request The discount type data to create or update
     * @return Success message with the discount type ID
     * @throws IllegalArgumentException if discount type not found during update
     */
    @Transactional
    public String upsertDiscountType(DiscountTypeUpsertDTO request) {
        log.info("[f:upsertDiscountType] Processing discount type upsert: {}", request);
        
        DiscountType discountType = getOrCreateDiscountType(request);
        updateDiscountTypeFields(discountType, request);
        
        discountTypeRepository.save(discountType);
        log.info("[f:upsertDiscountType] Discount type saved with ID: {}", discountType.getId());
        return "DiscountType upserted successfully with ID: " + discountType.getId();
    }

    /**
     * Retrieves existing discount type or creates a new one.
     *
     * @param request The upsert request
     * @return Discount type entity
     * @throws IllegalArgumentException if discount type not found during update
     */
    private DiscountType getOrCreateDiscountType(DiscountTypeUpsertDTO request) {
        if (Objects.nonNull(request.getId())) {
            return discountTypeRepository.findById(request.getId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "DiscountType not found with ID: " + request.getId()));
        }
        return new DiscountType();
    }

    /**
     * Updates discount type fields from the request.
     *
     * @param discountType The entity to update
     * @param request The request containing new values
     */
    private void updateDiscountTypeFields(DiscountType discountType, DiscountTypeUpsertDTO request) {
        discountType.setDiscountName(request.getDiscountName());
        discountType.setDiscountType(request.getDiscountType());
        discountType.setRuleFormat(request.getRuleFormat());
        discountType.setDescription(request.getDescription());
        discountType.setValidFrom(request.getValidFrom());
        discountType.setValidUntil(request.getValidUntil());

        if (Objects.nonNull(request.getStatus())) {
            discountType.setStatus(request.getStatus());
        }
    }

    /**
     * Searches discount types using the search API.
     *
     * @param searchDTO The search criteria
     * @return Search response with filtered discount types
     */
    public DiscountTypeSearchResponseDTO searchDiscountTypes(DiscountTypeSearchDTO searchDTO) {
        log.info("[f:searchDiscountTypes] Searching discount types with criteria: {}", searchDTO);
        return searchDiscountTypeApi.search(searchDTO);
    }
}
