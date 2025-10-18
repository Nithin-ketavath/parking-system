package com.apc.parking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "pricing")
public class Pricing {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pricing_seq")
    @SequenceGenerator(name = "pricing_seq", sequenceName = "pricing_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true)
    private String vehicleType; // "CAR", "BIKE", "TRUCK"

    @Column(nullable = false)
    private Double hourlyRate;

    public Pricing() {
    }

    public Pricing(String vehicleType, Double hourlyRate) {
        this.vehicleType = vehicleType;
        this.hourlyRate = hourlyRate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType == null ? null : vehicleType.trim().toUpperCase();
    }

    public Double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(Double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }
}
