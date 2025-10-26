package com.subscription.core.entity;

import com.subscription.core.enums.ZoneDirection;
import com.subscription.core.enums.ZoneStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

/**
 * Zone entity representing delivery zones with geographical and operational information.
 */
@Entity
@Table(name = "zones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Zone extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "zone_id", length = 64)
    private String zoneId;

    @Column(name = "district", nullable = false)
    private String district;

    @Column(name = "no_of_customers", nullable = false)
    private Integer noOfCustomers;

    @Column(name = "no_of_deliveries", nullable = false)
    private Integer noOfDeliveries;

    @Column(name = "zone_name", unique = true, nullable = false)
    private String zoneName;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction", nullable = false)
    private ZoneDirection direction;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ZoneStatus status;
}
