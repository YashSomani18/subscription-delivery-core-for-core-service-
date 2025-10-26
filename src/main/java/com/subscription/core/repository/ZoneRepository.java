package com.subscription.core.repository;

import com.subscription.core.entity.Zone;
import com.subscription.core.enums.ZoneStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, UUID> {

    Optional<Zone> findByZoneName(String zoneName);
    List<Zone> findByStatus(ZoneStatus status);
    List<Zone> findByDistrict(String district);
    boolean existsByZoneName(String zoneName);
}
