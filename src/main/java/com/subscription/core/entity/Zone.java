package com.subscription.core.entity;

import com.subscription.core.enums.ZoneDirection;
import com.subscription.core.enums.ZoneStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import jakarta.persistence.*;

/**
 * Zone entity representing delivery zones with geographical and operational information.
 */
@Entity
@Table(name = "zones")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Zone extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "zone_id", length = 64)
    String zoneId;

    @Column(name = "zone_code", unique = true, columnDefinition = "varchar(50)")
    String zoneCode;

    @Column(name = "district", nullable = false, columnDefinition = "varchar(255)")
    String district;

    @Column(name = "zone_name", unique = true, nullable = false, columnDefinition = "varchar(255)")
    String zoneName;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction", nullable = false)
    ZoneDirection direction;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    ZoneStatus status;
}
