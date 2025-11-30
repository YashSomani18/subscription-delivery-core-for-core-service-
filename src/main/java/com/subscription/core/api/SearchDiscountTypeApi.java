package com.subscription.core.api;

import com.subscription.core.dto.DiscountTypeResponseDTO;
import com.subscription.core.dto.DiscountTypeSearchDTO;
import com.subscription.core.dto.DiscountTypeSearchResponseDTO;
import com.subscription.core.entity.DiscountType;
import com.subscription.core.enums.DiscountStatus;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Search API service for discount types using JPA Criteria API.
 */
@Service
@Slf4j
public class SearchDiscountTypeApi {

    @Autowired
    private EntityManager em;

    private static final int DEFAULT_PAGE_OFFSET = 0;
    private static final int DEFAULT_PAGE_SIZE = 50;

    /**
     * Searches discount types based on provided filters.
     *
     * @param request The search request containing filters and pagination
     * @return Search response with filtered discount types and pagination info
     */
    @Transactional
    public DiscountTypeSearchResponseDTO search(DiscountTypeSearchDTO request) {
        log.info("[f:search] Searching discount types with filters: {}", request);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DiscountType> cq = cb.createQuery(DiscountType.class);
        Predicate mainCondition = cb.conjunction();

        Root<DiscountType> variableRoot = cq.from(DiscountType.class);

        mainCondition = addFilters(request, cb, mainCondition, variableRoot);

        cq.where(mainCondition);
        cq.orderBy(cb.desc(variableRoot.get("createdOn")));

        TypedQuery<DiscountType> query = em.createQuery(cq);

        if (Objects.nonNull(request.getAddPagination()) && request.getAddPagination()) {
            addPagination(request, query);
        }

        List<DiscountType> discountTypes = query.getResultList();
        int totalResults = em.createQuery(cq).getResultList().size();

        List<DiscountTypeResponseDTO> discountTypeDtos = discountTypes.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return DiscountTypeSearchResponseDTO.builder()
                .pageNumber(request.getPageNo())
                .pageSize(request.getPageSize())
                .totalResults(totalResults)
                .discountTypes(discountTypeDtos)
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
    private Predicate addFilters(DiscountTypeSearchDTO request, CriteriaBuilder cb,
                                Predicate mainCondition, Root<DiscountType> variableRoot) {
        if (StringUtils.hasText(request.getDiscountName())) {
            Predicate discountNamePredicate = cb.like(
                    cb.lower(variableRoot.get("discountName")),
                    "%" + request.getDiscountName().toLowerCase() + "%");
            mainCondition = cb.and(mainCondition, discountNamePredicate);
        }

        if (Objects.nonNull(request.getDiscountType())) {
            Predicate discountTypePredicate = cb.equal(
                    variableRoot.get("discountType"), request.getDiscountType());
            mainCondition = cb.and(mainCondition, discountTypePredicate);
        }

        if (Objects.nonNull(request.getStatus())) {
            Predicate statusPredicate = cb.equal(
                    variableRoot.get("status"), request.getStatus());
            mainCondition = cb.and(mainCondition, statusPredicate);
        }

        return mainCondition;
    }

    /**
     * Adds pagination to the query.
     *
     * @param request The search request with pagination parameters
     * @param query The typed query to paginate
     */
    private void addPagination(DiscountTypeSearchDTO request, TypedQuery<DiscountType> query) {
        int pageNo = Objects.isNull(request.getPageNo()) ? DEFAULT_PAGE_OFFSET : request.getPageNo();
        int pageSize = Objects.isNull(request.getPageSize()) ? DEFAULT_PAGE_SIZE : request.getPageSize();

        int startFrom = pageNo * pageSize;
        query.setFirstResult(startFrom);
        query.setMaxResults(pageSize);
    }

    /**
     * Converts DiscountType entity to response DTO.
     *
     * @param discountType The discount type entity
     * @return Response DTO
     */
    private DiscountTypeResponseDTO toDto(DiscountType discountType) {
        return DiscountTypeResponseDTO.builder()
                .id(discountType.getId())
                .discountType(discountType.getDiscountType())
                .ruleFormat(discountType.getRuleFormat())
                .discountName(discountType.getDiscountName())
                .description(discountType.getDescription())
                .validFrom(discountType.getValidFrom())
                .validUntil(discountType.getValidUntil())
                .status(discountType.getStatus())
                .build();
    }
}

