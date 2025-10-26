package com.subscription.core.repository;

import com.subscription.core.entity.ProductCategory;
import com.subscription.core.enums.CategoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, UUID> {

    List<ProductCategory> findByProductId(UUID productId);
    List<ProductCategory> findByStatus(CategoryStatus status);
    List<ProductCategory> findByCategory(String category);
}
