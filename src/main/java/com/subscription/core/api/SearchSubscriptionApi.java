package com.subscription.core.api;

import com.subscription.core.dto.SubscriptionResponseDTO;
import com.subscription.core.dto.SubscriptionSearchDTO;
import com.subscription.core.dto.SubscriptionSearchResponseDTO;
import com.subscription.core.entity.Subscription;
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
 * Search API service for subscriptions using JPA Criteria API.
 */
@Service
@Slf4j
public class SearchSubscriptionApi {

    @Autowired
    private EntityManager em;

    private static final int DEFAULT_PAGE_OFFSET = 0;
    private static final int DEFAULT_PAGE_SIZE = 50;

    /**
     * Searches subscriptions based on provided filters.
     *
     * @param request The search request containing filters and pagination
     * @return Search response with filtered subscriptions and pagination info
     */
    @Transactional
    public SubscriptionSearchResponseDTO search(SubscriptionSearchDTO request) {
        log.info("[f:search] Searching subscriptions with filters: {}", request);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Subscription> cq = cb.createQuery(Subscription.class);
        Predicate mainCondition = cb.conjunction();

        Root<Subscription> variableRoot = cq.from(Subscription.class);

        mainCondition = addFilters(request, cb, mainCondition, variableRoot);

        cq.where(mainCondition);
        cq.orderBy(cb.desc(variableRoot.get("createdOn")));

        TypedQuery<Subscription> query = em.createQuery(cq);

        if (Objects.nonNull(request.getAddPagination()) && request.getAddPagination()) {
            addPagination(request, query);
        }

        List<Subscription> subscriptions = query.getResultList();
        int totalResults = em.createQuery(cq).getResultList().size();

        List<SubscriptionResponseDTO> subscriptionDtos = subscriptions.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return SubscriptionSearchResponseDTO.builder()
                .pageNumber(request.getPageNo())
                .pageSize(request.getPageSize())
                .totalResults(totalResults)
                .subscriptions(subscriptionDtos)
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
    private Predicate addFilters(SubscriptionSearchDTO request, CriteriaBuilder cb,
                                Predicate mainCondition, Root<Subscription> variableRoot) {
        if (StringUtils.hasText(request.getUserId())) {
            Predicate userIdPredicate = cb.equal(
                    variableRoot.get("userId"), request.getUserId());
            mainCondition = cb.and(mainCondition, userIdPredicate);
        }

        if (StringUtils.hasText(request.getSlotId())) {
            Predicate slotIdPredicate = cb.equal(
                    variableRoot.get("slotId"), request.getSlotId());
            mainCondition = cb.and(mainCondition, slotIdPredicate);
        }

        if (Objects.nonNull(request.getStatus())) {
            Predicate statusPredicate = cb.equal(
                    variableRoot.get("status"), request.getStatus());
            mainCondition = cb.and(mainCondition, statusPredicate);
        }

        if (Objects.nonNull(request.getFrequency())) {
            Predicate frequencyPredicate = cb.equal(
                    variableRoot.get("frequency"), request.getFrequency());
            mainCondition = cb.and(mainCondition, frequencyPredicate);
        }

        return mainCondition;
    }

    /**
     * Adds pagination to the query.
     *
     * @param request The search request with pagination parameters
     * @param query The typed query to paginate
     */
    private void addPagination(SubscriptionSearchDTO request, TypedQuery<Subscription> query) {
        int pageNo = Objects.isNull(request.getPageNo()) ? DEFAULT_PAGE_OFFSET : request.getPageNo();
        int pageSize = Objects.isNull(request.getPageSize()) ? DEFAULT_PAGE_SIZE : request.getPageSize();

        int startFrom = pageNo * pageSize;
        query.setFirstResult(startFrom);
        query.setMaxResults(pageSize);
    }

    /**
     * Converts Subscription entity to response DTO.
     *
     * @param subscription The subscription entity
     * @return Response DTO
     */
    private SubscriptionResponseDTO toDto(Subscription subscription) {
        return SubscriptionResponseDTO.builder()
                .subscriptionId(subscription.getSubscriptionId())
                .userId(subscription.getUserId())
                .slotId(subscription.getSlotId())
                .deliveryAddressId(subscription.getDeliveryAddressId())
                .productIds(subscription.getProductIds())
                .frequency(subscription.getFrequency())
                .status(subscription.getStatus())
                .startDate(subscription.getStartDate())
                .nextDeliveryDate(subscription.getNextDeliveryDate())
                .totalAmount(subscription.getTotalAmount())
                .deliveriesCompleted(subscription.getDeliveriesCompleted())
                .deliveriesFailed(subscription.getDeliveriesFailed())
                .pausedAt(subscription.getPausedAt())
                .pauseReason(subscription.getPauseReason())
                .build();
    }
}
