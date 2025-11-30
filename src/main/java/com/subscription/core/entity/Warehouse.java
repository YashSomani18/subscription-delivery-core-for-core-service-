package com.subscription.core.entity;

import com.subscription.core.enums.WarehouseStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import jakarta.persistence.*;

@Entity
@Table(name = "warehouses")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Warehouse extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "warehouse_id", length = 64)
    String warehouseId;

    @Column(name = "warehouse_name", nullable = false, columnDefinition = "varchar(255)")
    String warehouseName;

    @Column(name = "warehouse_code", unique = true, nullable = false, columnDefinition = "varchar(255)")
    String warehouseCode;

    @Column(name = "address", nullable = false, columnDefinition = "varchar(500)")
    String address;

    @Column(name = "city", nullable = false, columnDefinition = "varchar(255)")
    String city;

    @Column(name = "state", nullable = false, columnDefinition = "varchar(255)")
    String state;

    @Column(name = "postal_code", nullable = false, columnDefinition = "varchar(255)")
    String postalCode;

    @Column(name = "latitude", nullable = false)
    Double latitude;

    @Column(name = "longitude", nullable = false)
    Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    WarehouseStatus status;
}
