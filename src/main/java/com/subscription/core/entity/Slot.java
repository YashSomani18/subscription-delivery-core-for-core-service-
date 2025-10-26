package com.subscription.core.entity;

import com.subscription.core.enums.SlotStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Slot entity representing delivery time slots with capacity and booking information.
 */
@Entity
@Table(name = "slots")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Slot extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "slot_id", length = 64)
    private String slotId;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "slot_date", nullable = false)
    private LocalDate slotDate;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "current_bookings", nullable = false)
    private Integer currentBookings;

    @Column(name = "zone_id", nullable = false)
    private String zoneId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SlotStatus status;
}
