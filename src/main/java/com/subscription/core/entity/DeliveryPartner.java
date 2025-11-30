package com.subscription.core.entity;

import com.subscription.core.enums.DeliveryPartnerCurrentStatus;
import com.subscription.core.enums.EmploymentStatus;
import com.subscription.core.enums.VehicleType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * DeliveryPartner entity representing delivery partners with vehicle and performance information.
 */
@Entity
@Table(name = "delivery_partners")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeliveryPartner extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "delivery_partner_id", length = 64)
    String deliveryPartnerId;

    @Column(name = "partner_name", nullable = false, columnDefinition = "varchar(255)")
    String partnerName;

    @Column(name = "email", unique = true, nullable = false, columnDefinition = "varchar(255)")
    String email;

    @Column(name = "phone_number", nullable = false, columnDefinition = "varchar(255)")
    String phoneNumber;

    @Column(name = "password_hash", nullable = false, columnDefinition = "varchar(255)")
    String passwordHash;

    @Column(name = "assigned_warehouse_id", columnDefinition = "varchar(255)")
    String assignedWarehouseId;

    @Column(name = "assigned_zone_id", columnDefinition = "varchar(255)")
    String assignedZoneId;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type")
    VehicleType vehicleType;

    @Column(name = "vehicle_number", columnDefinition = "varchar(255)")
    String vehicleNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status")
    EmploymentStatus employmentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_status")
    DeliveryPartnerCurrentStatus currentStatus;

    @Column(name = "total_deliveries")
    Integer totalDeliveries;

    @Column(name = "successful_deliveries")
    Integer successfulDeliveries;

    @Column(name = "average_rating", columnDefinition = "decimal(20,4)")
    BigDecimal averageRating;
}
