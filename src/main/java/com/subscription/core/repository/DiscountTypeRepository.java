package com.subscription.core.repository;

import com.subscription.core.entity.DiscountType;
import com.subscription.core.enums.DiscountCategory;
import com.subscription.core.enums.DiscountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DiscountTypeRepository extends JpaRepository<DiscountType, String> {

    List<DiscountType> findByStatus(DiscountStatus status);

    List<DiscountType> findByDiscountType(DiscountCategory discountType);

    Optional<DiscountType> findByDiscountName(String discountName);
}
