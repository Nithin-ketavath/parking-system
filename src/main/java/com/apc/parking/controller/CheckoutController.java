package com.apc.parking.controller;

import com.apc.parking.entity.Booking;
import com.apc.parking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/checkout")
@CrossOrigin(origins = "*")
public class CheckoutController {

    @Autowired
    private BookingService bookingService;

    // Search booking by slot
    @GetMapping("/search")
    public ResponseEntity<?> searchBooking(@RequestParam Long slotId) {
        try {
            Booking booking = bookingService.searchBooking(slotId);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage() != null ? e.getMessage() : ("No booking found for slot " + slotId)));
        }
    }

    // Checkout and free slot
    @PostMapping
    public ResponseEntity<?> checkout(@RequestParam Long slotId) {
        try {
            Booking result = bookingService.checkoutBySlot(slotId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error during checkout: " + (e.getMessage() == null ? "unknown" : e.getMessage())));
        }
    }
}
