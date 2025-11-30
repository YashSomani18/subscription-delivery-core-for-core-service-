package com.subscription.core.repository;

import com.subscription.core.entity.WarehouseZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseZoneRepository extends JpaRepository<WarehouseZone, String> {

    List<WarehouseZone> findByWarehouseId(String warehouseId);

    List<WarehouseZone> findByZoneId(String zoneId);

    Optional<WarehouseZone> findByWarehouseIdAndZoneId(String warehouseId, String zoneId);

    Optional<WarehouseZone> findByZoneIdAndIsPrimaryTrue(String zoneId);

    List<WarehouseZone> findByWarehouseIdAndIsPrimaryTrue(String warehouseId);
}

