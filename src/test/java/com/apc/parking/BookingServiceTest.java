package com.apc.parking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDateTime;

import com.apc.parking.entity.Booking;
import com.apc.parking.entity.BookingStatus;
import com.apc.parking.entity.ParkingSlot;
import com.apc.parking.entity.Payment;
import com.apc.parking.entity.PaymentStatus;
import com.apc.parking.entity.SlotStatus;
import com.apc.parking.entity.User;
import com.apc.parking.repository.ParkingSlotRepository;
import com.apc.parking.repository.PaymentRepository;
import com.apc.parking.repository.UserRepository;
import com.apc.parking.repository.VehicleRepository;
import com.apc.parking.service.BookingService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookingServiceTest {
    @Autowired
    BookingService bookingService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ParkingSlotRepository slotRepository;
    @Autowired
    VehicleRepository vehicleRepository;
    @Autowired
    PaymentRepository paymentRepository;

    @Test
    void testBookingAndCheckoutTracksTimeAndMocksPayment() {
        // Setup user and slot
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("pass");
        user.setEmail("testuser@example.com");
        user.setFullName("Test User");
        user.setPhoneNumber("1234567890");
        user.setRole(com.apc.parking.entity.Role.USER);
        userRepository.save(user);

        ParkingSlot slot = new ParkingSlot();
        slot.setSlotNumber("A1");
        slot.setLocation("Basement");
        slot.setStatus(SlotStatus.AVAILABLE);
        slot.setHourlyRate(100.0);
        slot.setVehicleType("CAR");
        slotRepository.save(slot);

        // Book slot
        Booking booking = bookingService.bookSlot(user.getUserId(), slot.getSlotId(), "ABC123");
        assertNotNull(booking.getBookingTime());
        assertNotNull(booking.getCheckInTime());
        assertEquals(BookingStatus.CHECKED_IN, booking.getStatus());

        // Simulate 2 hours parking by checking out after 2 hours
        // (simulate by setting checkInTime 2 hours ago, then check out)
        // We'll use reflection to set checkInTime for test purposes
        java.lang.reflect.Field checkInField;
        try {
            checkInField = Booking.class.getDeclaredField("checkInTime");
            checkInField.setAccessible(true);
            checkInField.set(booking, LocalDateTime.now().minusHours(2));
        } catch (Exception e) {
            fail("Reflection failed: " + e.getMessage());
        }

        Booking checkedOut = bookingService.checkOut(booking.getBookingId());
        assertNotNull(checkedOut.getCheckOutTime());
        assertEquals(BookingStatus.CHECKED_OUT, checkedOut.getStatus());
        assertTrue(checkedOut.getTotalCost() >= 200.0);

        // Payment should be mocked and completed
        Payment payment = checkedOut.getPayment();
        assertNotNull(payment);
        assertEquals(PaymentStatus.COMPLETED, payment.getStatus());
        assertEquals(checkedOut.getTotalCost(), payment.getAmount());
    }
}
