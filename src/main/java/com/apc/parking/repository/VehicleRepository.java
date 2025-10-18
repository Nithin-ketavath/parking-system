package com.apc.parking.repository;

import com.apc.parking.entity.Vehicle;
import com.apc.parking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByOwner(User owner);
    Optional<Vehicle> findByVehicleNumber(String vehicleNumber);
}
