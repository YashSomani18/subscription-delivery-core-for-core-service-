package com.subscription.core.repository;

import com.subscription.core.entity.Warehouse;
import com.subscription.core.enums.WarehouseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, String> {

    Optional<Warehouse> findByWarehouseCode(String warehouseCode);

    Optional<Warehouse> findByWarehouseName(String warehouseName);

    List<Warehouse> findByStatus(WarehouseStatus status);

    List<Warehouse> findByCity(String city);
}
