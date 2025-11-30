package com.subscription.core.api;

import com.subscription.core.dto.CategoryResponseDTO;
import com.subscription.core.dto.CategorySearchDTO;
import com.subscription.core.dto.CategorySearchResponseDTO;
import com.subscription.core.entity.Category;
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
 * Search API service for categories using JPA Criteria API.
 */
@Service
@Slf4j
public class SearchCategoryApi {

    @Autowired
    private EntityManager em;

    private static final int DEFAULT_PAGE_OFFSET = 0;
    private static final int DEFAULT_PAGE_SIZE = 50;

    /**
     * Searches categories based on provided filters.
     *
     * @param request The search request containing filters and pagination
     * @return Search response with filtered categories and pagination info
     */
    @Transactional
    public CategorySearchResponseDTO search(CategorySearchDTO request) {
        log.info("[f:search] Searching categories with filters: {}", request);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Category> cq = cb.createQuery(Category.class);
        Predicate mainCondition = cb.conjunction();

        Root<Category> variableRoot = cq.from(Category.class);

        mainCondition = addFilters(request, cb, mainCondition, variableRoot);

        cq.where(mainCondition);
        cq.orderBy(cb.asc(variableRoot.get("displayOrder")), cb.desc(variableRoot.get("createdOn")));

        TypedQuery<Category> query = em.createQuery(cq);

        if (Objects.nonNull(request.getAddPagination()) && request.getAddPagination()) {
            addPagination(request, query);
        }

        List<Category> categories = query.getResultList();
        int totalResults = em.createQuery(cq).getResultList().size();

        List<CategoryResponseDTO> categoryDtos = categories.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return CategorySearchResponseDTO.builder()
                .pageNumber(request.getPageNo())
                .pageSize(request.getPageSize())
                .totalResults(totalResults)
                .categories(categoryDtos)
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
    private Predicate addFilters(CategorySearchDTO request, CriteriaBuilder cb,
                                Predicate mainCondition, Root<Category> variableRoot) {
        if (StringUtils.hasText(request.getName())) {
            Predicate namePredicate = cb.like(
                    cb.lower(variableRoot.get("name")),
                    "%" + request.getName().toLowerCase() + "%");
            mainCondition = cb.and(mainCondition, namePredicate);
        }

        if (Objects.nonNull(request.getIsActive())) {
            Predicate isActivePredicate = cb.equal(
                    variableRoot.get("isActive"), request.getIsActive());
            mainCondition = cb.and(mainCondition, isActivePredicate);
        }

        return mainCondition;
    }

    /**
     * Adds pagination to the query.
     *
     * @param request The search request with pagination parameters
     * @param query The typed query to paginate
     */
    private void addPagination(CategorySearchDTO request, TypedQuery<Category> query) {
        int pageNo = Objects.isNull(request.getPageNo()) ? DEFAULT_PAGE_OFFSET : request.getPageNo();
        int pageSize = Objects.isNull(request.getPageSize()) ? DEFAULT_PAGE_SIZE : request.getPageSize();

        int startFrom = pageNo * pageSize;
        query.setFirstResult(startFrom);
        query.setMaxResults(pageSize);
    }

    /**
     * Converts Category entity to response DTO.
     *
     * @param category The category entity
     * @return Response DTO
     */
    private CategoryResponseDTO toDto(Category category) {
        return CategoryResponseDTO.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .gstSlab(category.getGstSlab())
                .displayOrder(category.getDisplayOrder())
                .isActive(category.getIsActive())
                .build();
    }
}

