package com.subscription.core.repository;

import com.subscription.core.entity.ProductCategory;
import com.subscription.core.enums.CategoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, String> {

    List<ProductCategory> findByProductId(String productId);
    List<ProductCategory> findByStatus(CategoryStatus status);
    List<ProductCategory> findByCategory(String category);
}
