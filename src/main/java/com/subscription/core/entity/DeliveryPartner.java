package com.subscription.core.entity;

import com.subscription.core.enums.DeliveryPartnerCurrentStatus;
import com.subscription.core.enums.EmploymentStatus;
import com.subscription.core.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "delivery_partners")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPartner extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "delivery_partner_id", length = 64)
    private String deliveryPartnerId;

    @Column(name = "partner_name", nullable = false, length = 100)
    private String partnerName;

    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "assigned_warehouse_id")
    private String assignedWarehouseId;

    @Column(name = "assigned_zone_id")
    private String assignedZoneId;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type")
    private VehicleType vehicleType;

    @Column(name = "vehicle_number", length = 20)
    private String vehicleNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status")
    private EmploymentStatus employmentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_status")
    private DeliveryPartnerCurrentStatus currentStatus;

    @Column(name = "total_deliveries")
    private Integer totalDeliveries;

    @Column(name = "successful_deliveries")
    private Integer successfulDeliveries;

    @Column(name = "average_rating")
    private BigDecimal averageRating;
}
