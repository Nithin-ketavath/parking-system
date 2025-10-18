package com.apc.parking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDateTime;
import java.util.Optional;

import com.apc.parking.entity.Booking;
import com.apc.parking.entity.BookingStatus;
import com.apc.parking.entity.ParkingSlot;
import com.apc.parking.entity.SlotStatus;
import com.apc.parking.repository.BookingRepository;
import com.apc.parking.repository.ParkingSlotRepository;
import com.apc.parking.repository.UserRepository;
import com.apc.parking.service.BookingServiceImpl;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class BookingServiceImplTest {
    @Test
    void testCheckOutSetsStatusAndCost() {
        BookingRepository bookingRepo = Mockito.mock(BookingRepository.class);
        ParkingSlotRepository slotRepo = Mockito.mock(ParkingSlotRepository.class);
        UserRepository userRepo = Mockito.mock(UserRepository.class);
        BookingServiceImpl service = new BookingServiceImpl();
        // Use reflection to inject mocks
        try {
            var f1 = BookingServiceImpl.class.getDeclaredField("bookingRepository");
            f1.setAccessible(true);
            f1.set(service, bookingRepo);
            var f2 = BookingServiceImpl.class.getDeclaredField("parkingSlotRepository");
            f2.setAccessible(true);
            f2.set(service, slotRepo);
            var f3 = BookingServiceImpl.class.getDeclaredField("userRepository");
            f3.setAccessible(true);
            f3.set(service, userRepo);
        } catch (Exception e) {
            fail("Reflection error: " + e.getMessage());
        }
        ParkingSlot slot = new ParkingSlot();
        slot.setHourlyRate(100.0);
        slot.setStatus(SlotStatus.OCCUPIED);
        Booking booking = new Booking();
        booking.setBookingId(1L);
        booking.setSlot(slot);
        booking.setCheckInTime(LocalDateTime.now().minusHours(2));
        booking.setStatus(BookingStatus.CHECKED_IN);
        Mockito.when(bookingRepo.findById(1L)).thenReturn(Optional.of(booking));
        Mockito.when(bookingRepo.save(Mockito.any())).thenAnswer(i -> i.getArgument(0));
        Mockito.when(slotRepo.save(Mockito.any())).thenAnswer(i -> i.getArgument(0));
        Booking checkedOut = service.checkOut(1L);
        assertEquals(BookingStatus.CHECKED_OUT, checkedOut.getStatus());
        assertTrue(checkedOut.getTotalCost() >= 200.0);
        assertNotNull(checkedOut.getCheckOutTime());
    }
}
