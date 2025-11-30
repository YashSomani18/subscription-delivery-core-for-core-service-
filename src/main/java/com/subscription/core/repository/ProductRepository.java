package com.subscription.core.repository;

import com.subscription.core.entity.Product;
import com.subscription.core.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    List<Product> findByCategoryId(String categoryId);

    Optional<Product> findBySku(String sku);

    List<Product> findByStatus(ProductStatus status);

    Page<Product> findByStatus(ProductStatus status, Pageable pageable);

    List<Product> findByProductNameContainingIgnoreCase(String productName);

    boolean existsBySku(String sku);

    @Query("SELECT p FROM Product p WHERE (p.status = :status AND LOWER(p.productName) LIKE LOWER(CONCAT('%', :query, '%'))) OR (p.status = :status AND LOWER(p.sku) = LOWER(:query))")
    Page<Product> findByStatusAndProductNameContainingIgnoreCaseOrStatusAndSkuIgnoreCase(
            @Param("status") ProductStatus status,
            @Param("query") String query,
            Pageable pageable);
}
