package com.apc.parking.repository;

import com.apc.parking.entity.ParkingSlot;
import com.apc.parking.entity.SlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {
    Optional<ParkingSlot> findBySlotNumber(String slotNumber);

    List<ParkingSlot> findByStatus(SlotStatus status);

    List<ParkingSlot> findByVehicleType(String vehicleType);

    List<ParkingSlot> findByStatusAndVehicleType(SlotStatus status, String vehicleType);
}
