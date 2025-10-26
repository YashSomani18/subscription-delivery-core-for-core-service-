package com.subscription.core.repository;

import com.subscription.core.entity.DeliveryPartner;
import com.subscription.core.enums.DeliveryPartnerCurrentStatus;
import com.subscription.core.enums.EmploymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryPartnerRepository extends JpaRepository<DeliveryPartner, String> {

    Optional<DeliveryPartner> findByEmail(String email);
    List<DeliveryPartner> findByEmploymentStatus(EmploymentStatus employmentStatus);
    List<DeliveryPartner> findByCurrentStatus(DeliveryPartnerCurrentStatus currentStatus);
    List<DeliveryPartner> findByAssignedZoneId(String zoneId);
    boolean existsByEmail(String email);
}
