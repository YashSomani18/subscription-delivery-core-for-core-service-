package com.subscription.core.api;

import com.subscription.core.dto.ProductResponseDTO;
import com.subscription.core.dto.ProductSearchDTO;
import com.subscription.core.dto.ProductSearchResponseDTO;
import com.subscription.core.entity.Product;
import com.subscription.core.enums.ProductStatus;
import com.subscription.core.service.EntityMapper;
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
 * Search API service for products using JPA Criteria API.
 */
@Service
@Slf4j
public class SearchProductApi {

    @Autowired
    private EntityManager em;

    @Autowired
    private EntityMapper entityMapper;

    private static final int DEFAULT_PAGE_OFFSET = 0;
    private static final int DEFAULT_PAGE_SIZE = 50;

    /**
     * Searches products based on provided filters.
     *
     * @param request The search request containing filters and pagination
     * @return Search response with filtered products and pagination info
     */
    @Transactional
    public ProductSearchResponseDTO search(ProductSearchDTO request) {
        log.info("[f:search] Searching products with filters: {}", request);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Predicate mainCondition = cb.conjunction();

        Root<Product> variableRoot = cq.from(Product.class);

        mainCondition = addFilters(request, cb, mainCondition, variableRoot);

        cq.where(mainCondition);
        cq.orderBy(cb.desc(variableRoot.get("createdOn")));

        TypedQuery<Product> query = em.createQuery(cq);

        if (Objects.nonNull(request.getAddPagination()) && request.getAddPagination()) {
            addPagination(request, query);
        }

        List<Product> products = query.getResultList();
        int totalResults = em.createQuery(cq).getResultList().size();

        List<ProductResponseDTO> productDtos = products.stream()
                .map(entityMapper::toProductResponseDTO)
                .collect(Collectors.toList());

        return ProductSearchResponseDTO.builder()
                .pageNumber(request.getPageNo())
                .pageSize(request.getPageSize())
                .totalCount((long) totalResults)
                .products(productDtos)
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
    private Predicate addFilters(ProductSearchDTO request, CriteriaBuilder cb,
                                Predicate mainCondition, Root<Product> variableRoot) {
        Predicate statusPredicate = cb.equal(
                variableRoot.get("status"), ProductStatus.ACTIVE);
        mainCondition = cb.and(mainCondition, statusPredicate);

        if (StringUtils.hasText(request.getQuery())) {
            String queryLower = request.getQuery().toLowerCase();
            Predicate productNamePredicate = cb.like(
                    cb.lower(variableRoot.get("productName")), "%" + queryLower + "%");
            Predicate skuPredicate = cb.like(
                    cb.lower(variableRoot.get("sku")), "%" + queryLower + "%");
            Predicate queryPredicate = cb.or(productNamePredicate, skuPredicate);
            mainCondition = cb.and(mainCondition, queryPredicate);
        }

        if (StringUtils.hasText(request.getCategoryId())) {
            Predicate categoryPredicate = cb.equal(
                    variableRoot.get("categoryId"), request.getCategoryId());
            mainCondition = cb.and(mainCondition, categoryPredicate);
        }

        if (Objects.nonNull(request.getIsPerishable())) {
            Predicate perishablePredicate = cb.equal(
                    variableRoot.get("isPerishable"), request.getIsPerishable());
            mainCondition = cb.and(mainCondition, perishablePredicate);
        }

        return mainCondition;
    }

    /**
     * Adds pagination to the query.
     *
     * @param request The search request with pagination parameters
     * @param query The typed query to paginate
     */
    private void addPagination(ProductSearchDTO request, TypedQuery<Product> query) {
        int pageNo = Objects.isNull(request.getPageNo()) ? DEFAULT_PAGE_OFFSET : request.getPageNo();
        int pageSize = Objects.isNull(request.getPageSize()) ? DEFAULT_PAGE_SIZE : request.getPageSize();

        int startFrom = pageNo * pageSize;
        query.setFirstResult(startFrom);
        query.setMaxResults(pageSize);
    }
}

