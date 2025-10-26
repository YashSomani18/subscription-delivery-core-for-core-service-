package com.subscription.core.entity;

import com.subscription.core.enums.WarehouseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

/**
 * Warehouse entity representing storage facilities with location and operational information.
 */
@Entity
@Table(name = "warehouses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Warehouse extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "warehouse_id", length = 64)
    private String warehouseId;

    @Column(name = "warehouse_name", nullable = false)
    private String warehouseName;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "postal_code", nullable = false)
    private String postalCode;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "zone_id", nullable = false)
    private String zoneId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private WarehouseStatus status;
}
