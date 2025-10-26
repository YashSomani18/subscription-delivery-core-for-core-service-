package com.subscription.core.repository;

import com.subscription.core.entity.Warehouse;
import com.subscription.core.enums.WarehouseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {

    List<Warehouse> findByZoneId(UUID zoneId);
    List<Warehouse> findByStatus(WarehouseStatus status);
    Optional<Warehouse> findByWarehouseName(String warehouseName);
}
