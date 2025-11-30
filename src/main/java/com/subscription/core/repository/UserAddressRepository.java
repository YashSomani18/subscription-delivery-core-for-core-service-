package com.subscription.core.repository;

import com.subscription.core.entity.UserAddress;
import com.subscription.core.enums.AddressStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, String> {

    List<UserAddress> findByUserId(String userId);
    List<UserAddress> findByStatus(AddressStatus status);
    List<UserAddress> findByZoneId(String zoneId);
    Optional<UserAddress> findByUserAddressId(String userAddressId);
}
