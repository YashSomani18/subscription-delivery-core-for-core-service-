package com.subscription.core.repository;

import com.subscription.core.entity.UserAddress;
import com.subscription.core.enums.AddressStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, UUID> {

    List<UserAddress> findByUserId(UUID userId);
    List<UserAddress> findByStatus(AddressStatus status);
    List<UserAddress> findByZoneId(UUID zoneId);
}
