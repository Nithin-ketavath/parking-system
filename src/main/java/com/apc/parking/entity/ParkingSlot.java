
package com.apc.parking.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "parking_slots")
@JsonIgnoreProperties({ "bookings", "hibernateLazyInitializer", "handler" })
public class ParkingSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "slot_seq")
    @SequenceGenerator(name = "slot_seq", sequenceName = "slot_seq", allocationSize = 1)
    private Long slotId;

    @Column(nullable = false, unique = true)
    private String slotNumber;

    @Column(nullable = false)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SlotStatus status = SlotStatus.AVAILABLE;

    @Column(nullable = false)
    private Double hourlyRate = 50.0;

    @Column(nullable = false)
    private String vehicleType = "CAR"; // CAR, BIKE, TRUCK

    @OneToMany(mappedBy = "slot", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings;

    // --- Constructors ---
    public ParkingSlot() {
    }

    public ParkingSlot(String slotNumber, String location, String vehicleType) {
        this.slotNumber = slotNumber;
        this.location = location;
        this.vehicleType = vehicleType;
    }

    // --- Getters and Setters ---
    public Long getSlotId() {
        return slotId;
    }

    public void setSlotId(Long slotId) {
        this.slotId = slotId;
    }

    public String getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(String slotNumber) {
        this.slotNumber = slotNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public SlotStatus getStatus() {
        return status;
    }

    public void setStatus(SlotStatus status) {
        this.status = status;
    }

    public Double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(Double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }
}
