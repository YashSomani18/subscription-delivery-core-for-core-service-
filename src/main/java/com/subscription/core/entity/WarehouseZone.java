package com.subscription.core.entity;

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
@Table(name = "warehouse_zones", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"warehouse_id", "zone_id"})
})
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WarehouseZone extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "warehouse_zone_id", length = 64)
    String warehouseZoneId;

    @Column(name = "warehouse_id", nullable = false, columnDefinition = "varchar(255)")
    String warehouseId;

    @Column(name = "zone_id", nullable = false, columnDefinition = "varchar(255)")
    String zoneId;

    @Column(name = "is_primary", nullable = false, columnDefinition = "TINYINT(1)")
    Boolean isPrimary;
}
