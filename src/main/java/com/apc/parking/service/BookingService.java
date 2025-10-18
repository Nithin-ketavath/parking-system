package com.apc.parking.service;

import java.time.LocalDateTime;
import java.util.List;

import com.apc.parking.entity.Booking;
import com.apc.parking.entity.ParkingSlot;
import com.apc.parking.entity.Payment;

public interface BookingService {
    List<Booking> getUserBookingsByUsername(String username);

    Booking bookSlot(Long userId, Long slotId, String vehicleNumber);

    Booking bookSlotByUsername(Long slotId, String username, String vehicleNumber);

    Booking checkIn(Long bookingId);

    Booking checkOut(Long bookingId);

    Booking cancelBooking(Long bookingId);

    List<Booking> getUserBookings(Long userId);

    List<Booking> getActiveBookings();

    List<ParkingSlot> getAvailableSlots();

    List<ParkingSlot> getAvailableSlotsByVehicleType(String vehicleType);

    Booking getBookingById(Long bookingId);

    double calculateCost(LocalDateTime checkInTime, LocalDateTime checkOutTime, double hourlyRate);

    java.util.List<Booking> getRecentBookings(String username);

    // New methods for checkout by slot
    Booking searchBooking(Long slotId);

    Booking checkoutBySlot(Long slotId);

    Payment checkout(Long slotId);
}
