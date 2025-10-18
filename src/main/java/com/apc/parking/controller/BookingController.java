package com.apc.parking.controller;

import java.security.Principal;
import java.util.List;

import com.apc.parking.entity.Booking;
import com.apc.parking.repository.UserRepository;
import com.apc.parking.service.BookingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    // List active (checked-in) bookings used by checkout UI
    @GetMapping("/active")
    public ResponseEntity<List<Booking>> getActiveBookings() {
        List<Booking> active = bookingService.getActiveBookings();
        return ResponseEntity.ok(active);
    }

    // Active bookings for current user
    @GetMapping("/my/active")
    public ResponseEntity<?> getMyActiveBookings(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("User not authenticated");
        }
        try {
            var username = principal.getName();
            List<Booking> bookings = bookingService.getUserBookingsByUsername(username);
            if (bookings.isEmpty()) {
                return ResponseEntity.ok(List.of()); // Return empty list for no bookings
            }
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error loading active bookings: " + e.getMessage());
        }
    }

    // Checkout a booking (process checkout)
    @PostMapping("/checkout/{bookingId}")
    public ResponseEntity<Booking> checkout(@PathVariable Long bookingId) {
        try {
            Booking result = bookingService.checkOut(bookingId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException iae) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException ise) {
            return ResponseEntity.status(409).build(); // conflict: invalid state
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // Reserve/book a slot (your existing route)
    @PostMapping("/reserve/{slotId}/user/{userId}")
    public ResponseEntity<Booking> reserve(@PathVariable Long slotId,
            @PathVariable Long userId,
            @RequestParam String vehicleNumber) {
        try {
            Booking booking = bookingService.bookSlot(userId, slotId, vehicleNumber);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    // Simple booking used by UI: /api/bookings/book/{slotId}?vehicleNumber=XYZ
    // Now requires authentication via Principal
    @PostMapping("/book/{slotId}")
    public ResponseEntity<Booking> book(@PathVariable Long slotId,
            @RequestParam String vehicleNumber,
            Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(401).body(null);
            }
            String username = principal.getName();
            var user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
            Booking booking = bookingService.bookSlot(user.getUserId(), slotId, vehicleNumber);
            return ResponseEntity.ok(booking);
        } catch (IllegalArgumentException iae) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException ise) {
            return ResponseEntity.status(409).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // Book by username (explicit user linkage)
    @PostMapping("/book/{slotId}/by-username")
    public ResponseEntity<Booking> bookByUsername(@PathVariable Long slotId,
            @RequestParam String username,
            @RequestParam String vehicleNumber) {
        try {
            if (username == null || username.isBlank()) {
                return ResponseEntity.badRequest().build();
            }
            Booking booking = bookingService.bookSlotByUsername(slotId, username, vehicleNumber);
            return ResponseEntity.ok(booking);
        } catch (IllegalArgumentException iae) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException ise) {
            return ResponseEntity.status(409).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // Recent bookings for logged-in user
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentBookings(Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(401).build();
            }
            List<Booking> bookings = bookingService.getRecentBookings(principal.getName());
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error loading bookings: " + e.getMessage());
        }
    }
}