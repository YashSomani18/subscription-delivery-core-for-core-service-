package com.subscription.core.repository;

import com.subscription.core.entity.Slot;
import com.subscription.core.enums.SlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface SlotRepository extends JpaRepository<Slot, UUID> {

    List<Slot> findByZoneId(UUID zoneId);
    List<Slot> findBySlotDate(LocalDate slotDate);
    List<Slot> findByStatus(SlotStatus status);
    List<Slot> findByZoneIdAndSlotDate(UUID zoneId, LocalDate slotDate);
}
