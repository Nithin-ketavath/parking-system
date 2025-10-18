package com.apc.parking.repository;

import java.util.Optional;

import com.apc.parking.entity.Pricing;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PricingRepository extends JpaRepository<Pricing, Long> {
    Optional<Pricing> findByVehicleType(String vehicleType);
}
