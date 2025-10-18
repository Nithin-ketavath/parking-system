package com.apc.parking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;

import com.apc.parking.entity.Booking;
import com.apc.parking.entity.BookingStatus;

import org.junit.jupiter.api.Test;

class BookingEntityTest {
    @Test
    void testBookingFields() {
        Booking booking = new Booking();
        booking.setBookingTime(LocalDateTime.now());
        booking.setCheckInTime(LocalDateTime.now());
        booking.setCheckOutTime(LocalDateTime.now().plusHours(2));
        booking.setTotalCost(200.0);
        booking.setStatus(BookingStatus.CHECKED_OUT);

        assertNotNull(booking.getBookingTime());
        assertNotNull(booking.getCheckInTime());
        assertNotNull(booking.getCheckOutTime());
        assertEquals(200.0, booking.getTotalCost());
        assertEquals(BookingStatus.CHECKED_OUT, booking.getStatus());
    }
}
