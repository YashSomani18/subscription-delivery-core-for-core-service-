package com.subscription.core.repository;

import com.subscription.core.entity.Product;
import com.subscription.core.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    Optional<Product> findBySku(String sku);
    List<Product> findByStatus(ProductStatus status);
    List<Product> findByProductNameContainingIgnoreCase(String productName);
    boolean existsBySku(String sku);
}
