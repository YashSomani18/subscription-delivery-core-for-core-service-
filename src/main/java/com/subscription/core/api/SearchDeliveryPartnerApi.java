package com.subscription.core.api;

import com.subscription.core.dto.DeliveryPartnerResponseDTO;
import com.subscription.core.dto.DeliveryPartnerSearchDTO;
import com.subscription.core.dto.DeliveryPartnerSearchResponseDTO;
import com.subscription.core.entity.DeliveryPartner;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Search API service for delivery partners using JPA Criteria API.
 */
@Service
@Slf4j
public class SearchDeliveryPartnerApi {

    @Autowired
    private EntityManager em;

    private static final int DEFAULT_PAGE_OFFSET = 0;
    private static final int DEFAULT_PAGE_SIZE = 50;

    /**
     * Searches delivery partners based on provided filters.
     *
     * @param request The search request containing filters and pagination
     * @return Search response with filtered delivery partners and pagination info
     */
    @Transactional
    public DeliveryPartnerSearchResponseDTO search(DeliveryPartnerSearchDTO request) {
        log.info("[f:search] Searching delivery partners with filters: {}", request);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DeliveryPartner> cq = cb.createQuery(DeliveryPartner.class);
        Predicate mainCondition = cb.conjunction();

        Root<DeliveryPartner> variableRoot = cq.from(DeliveryPartner.class);

        mainCondition = addFilters(request, cb, mainCondition, variableRoot);

        cq.where(mainCondition);
        cq.orderBy(cb.desc(variableRoot.get("createdOn")));

        TypedQuery<DeliveryPartner> query = em.createQuery(cq);

        if (Objects.nonNull(request.getAddPagination()) && request.getAddPagination()) {
            addPagination(request, query);
        }

        List<DeliveryPartner> deliveryPartners = query.getResultList();
        int totalResults = em.createQuery(cq).getResultList().size();

        List<DeliveryPartnerResponseDTO> deliveryPartnerDtos = deliveryPartners.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return DeliveryPartnerSearchResponseDTO.builder()
                .pageNumber(request.getPageNo())
                .pageSize(request.getPageSize())
                .totalResults(totalResults)
                .deliveryPartners(deliveryPartnerDtos)
                .build();
    }

    /**
     * Adds filters to the criteria query based on search request.
     *
     * @param request The search request
     * @param cb Criteria builder
     * @param mainCondition Current predicate condition
     * @param variableRoot Root entity
     * @return Updated predicate with filters applied
     */
    private Predicate addFilters(DeliveryPartnerSearchDTO request, CriteriaBuilder cb,
                                Predicate mainCondition, Root<DeliveryPartner> variableRoot) {
        if (StringUtils.hasText(request.getPartnerName())) {
            Predicate partnerNamePredicate = cb.like(
                    cb.lower(variableRoot.get("partnerName")),
                    "%" + request.getPartnerName().toLowerCase() + "%");
            mainCondition = cb.and(mainCondition, partnerNamePredicate);
        }

        if (StringUtils.hasText(request.getEmail())) {
            Predicate emailPredicate = cb.like(
                    cb.lower(variableRoot.get("email")),
                    "%" + request.getEmail().toLowerCase() + "%");
            mainCondition = cb.and(mainCondition, emailPredicate);
        }

        if (StringUtils.hasText(request.getAssignedZoneId())) {
            Predicate assignedZonePredicate = cb.equal(
                    variableRoot.get("assignedZoneId"), request.getAssignedZoneId());
            mainCondition = cb.and(mainCondition, assignedZonePredicate);
        }

        if (StringUtils.hasText(request.getAssignedWarehouseId())) {
            Predicate assignedWarehousePredicate = cb.equal(
                    variableRoot.get("assignedWarehouseId"), request.getAssignedWarehouseId());
            mainCondition = cb.and(mainCondition, assignedWarehousePredicate);
        }

        if (Objects.nonNull(request.getEmploymentStatus())) {
            Predicate employmentStatusPredicate = cb.equal(
                    variableRoot.get("employmentStatus"), request.getEmploymentStatus());
            mainCondition = cb.and(mainCondition, employmentStatusPredicate);
        }

        if (Objects.nonNull(request.getCurrentStatus())) {
            Predicate currentStatusPredicate = cb.equal(
                    variableRoot.get("currentStatus"), request.getCurrentStatus());
            mainCondition = cb.and(mainCondition, currentStatusPredicate);
        }

        return mainCondition;
    }

    /**
     * Adds pagination to the query.
     *
     * @param request The search request with pagination parameters
     * @param query The typed query to paginate
     */
    private void addPagination(DeliveryPartnerSearchDTO request, TypedQuery<DeliveryPartner> query) {
        int pageNo = Objects.isNull(request.getPageNo()) ? DEFAULT_PAGE_OFFSET : request.getPageNo();
        int pageSize = Objects.isNull(request.getPageSize()) ? DEFAULT_PAGE_SIZE : request.getPageSize();

        int startFrom = pageNo * pageSize;
        query.setFirstResult(startFrom);
        query.setMaxResults(pageSize);
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
