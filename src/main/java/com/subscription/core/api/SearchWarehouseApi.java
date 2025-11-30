package com.subscription.core.api;

import com.subscription.core.dto.WarehouseResponseDTO;
import com.subscription.core.dto.WarehouseSearchDTO;
import com.subscription.core.dto.WarehouseSearchResponseDTO;
import com.subscription.core.entity.Warehouse;
import com.subscription.core.enums.WarehouseStatus;
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

@Service
@Slf4j
public class SearchWarehouseApi {

    @Autowired
    private EntityManager em;

    private static final int DEFAULT_PAGE_OFFSET = 0;
    private static final int DEFAULT_PAGE_SIZE = 50;

    @Transactional
    public WarehouseSearchResponseDTO search(WarehouseSearchDTO request) {
        log.info("[f:search] Searching warehouses with filters: {}", request);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Warehouse> cq = cb.createQuery(Warehouse.class);
        Predicate mainCondition = cb.conjunction();

        Root<Warehouse> variableRoot = cq.from(Warehouse.class);

        mainCondition = addFilters(request, cb, mainCondition, variableRoot);

        cq.where(mainCondition);
        cq.orderBy(cb.desc(variableRoot.get("createdOn")));

        TypedQuery<Warehouse> query = em.createQuery(cq);

        if (Objects.nonNull(request.getAddPagination()) && request.getAddPagination()) {
            addPagination(request, query);
        }

        List<Warehouse> warehouses = query.getResultList();
        int totalResults = em.createQuery(cq).getResultList().size();

        List<WarehouseResponseDTO> warehouseDtos = warehouses.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return WarehouseSearchResponseDTO.builder()
                .pageNumber(request.getPageNo())
                .pageSize(request.getPageSize())
                .totalResults(totalResults)
                .warehouses(warehouseDtos)
                .build();
    }

    private Predicate addFilters(WarehouseSearchDTO request, CriteriaBuilder cb,
                                Predicate mainCondition, Root<Warehouse> variableRoot) {
        if (StringUtils.hasText(request.getWarehouseName())) {
            Predicate namePredicate = cb.like(
                    cb.lower(variableRoot.get("warehouseName")), 
                    "%" + request.getWarehouseName().toLowerCase() + "%");
            mainCondition = cb.and(mainCondition, namePredicate);
        }

        if (StringUtils.hasText(request.getWarehouseCode())) {
            Predicate codePredicate = cb.equal(
                    variableRoot.get("warehouseCode"), request.getWarehouseCode());
            mainCondition = cb.and(mainCondition, codePredicate);
        }

        if (StringUtils.hasText(request.getCity())) {
            Predicate cityPredicate = cb.like(
                    cb.lower(variableRoot.get("city")), 
                    "%" + request.getCity().toLowerCase() + "%");
            mainCondition = cb.and(mainCondition, cityPredicate);
        }

        if (StringUtils.hasText(request.getState())) {
            Predicate statePredicate = cb.like(
                    cb.lower(variableRoot.get("state")), 
                    "%" + request.getState().toLowerCase() + "%");
            mainCondition = cb.and(mainCondition, statePredicate);
        }

        if (Objects.nonNull(request.getStatus())) {
            Predicate statusPredicate = cb.equal(
                    variableRoot.get("status"), request.getStatus());
            mainCondition = cb.and(mainCondition, statusPredicate);
        }

        return mainCondition;
    }

    private void addPagination(WarehouseSearchDTO request, TypedQuery<Warehouse> query) {
        int pageNo = Objects.isNull(request.getPageNo()) ? DEFAULT_PAGE_OFFSET : request.getPageNo();
        int pageSize = Objects.isNull(request.getPageSize()) ? DEFAULT_PAGE_SIZE : request.getPageSize();

        int startFrom = pageNo * pageSize;
        query.setFirstResult(startFrom);
        query.setMaxResults(pageSize);
    }

    private WarehouseResponseDTO toDto(Warehouse warehouse) {
        return WarehouseResponseDTO.builder()
                .warehouseId(warehouse.getWarehouseId())
                .warehouseName(warehouse.getWarehouseName())
                .warehouseCode(warehouse.getWarehouseCode())
                .address(warehouse.getAddress())
                .city(warehouse.getCity())
                .state(warehouse.getState())
                .postalCode(warehouse.getPostalCode())
                .latitude(warehouse.getLatitude())
                .longitude(warehouse.getLongitude())
                .status(warehouse.getStatus())
                .build();
    }
}

