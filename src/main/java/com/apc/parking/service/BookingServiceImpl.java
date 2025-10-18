package com.apc.parking.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import com.apc.parking.entity.Booking;
import com.apc.parking.entity.BookingStatus;
import com.apc.parking.entity.ParkingSlot;
import com.apc.parking.entity.Payment;
import com.apc.parking.entity.PaymentStatus;
import com.apc.parking.entity.SlotStatus;
import com.apc.parking.entity.User;
import com.apc.parking.entity.Vehicle;
import com.apc.parking.repository.BookingRepository;
import com.apc.parking.repository.ParkingSlotRepository;
import com.apc.parking.repository.UserRepository;
import com.apc.parking.repository.VehicleRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingServiceImpl implements BookingService {
    private static final Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ParkingSlotRepository parkingSlotRepository;

    @Autowired(required = false)
    private com.apc.parking.repository.PaymentRepository paymentRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired(required = false)
    private com.apc.parking.repository.PricingRepository pricingRepository;

    @org.springframework.beans.factory.annotation.Value("${parking.defaultHourlyRate:50.0}")
    private double defaultHourlyRate;

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getUserBookingsByUsername(String username) {
        var userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty())
            return List.of();
        var user = userOpt.get();
        // Treat both BOOKED and CHECKED_IN as active for the user-facing "active
        // bookings" view
        // Use a fetch-join variant to initialize related entities and avoid
        // serialization errors
        return bookingRepository.findByUserAndStatusInWithDetails(user,
                java.util.List.of(BookingStatus.BOOKED, BookingStatus.CHECKED_IN));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getActiveBookings() {
        // returns bookings with status CHECKED_IN (active bookings) and fetches all
        // details
        return bookingRepository.findByStatusWithDetails(BookingStatus.CHECKED_IN);
    }

    @Override
    @Transactional
    public Booking checkOut(Long bookingId) {
        logger.info("checkOut called for bookingId={}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));

        if (booking.getStatus() != BookingStatus.CHECKED_IN) {
            throw new IllegalStateException("Booking is not in CHECKED_IN state, current: " + booking.getStatus());
        }

        LocalDateTime now = LocalDateTime.now();
        booking.setCheckOutTime(now);

        // determine hourly rate: prefer slot.rate, then pricing table by vehicle type,
        // else fallback to configured default
        Double rate = defaultHourlyRate; // default rate (configurable)
        ParkingSlot slot = booking.getSlot();
        if (slot != null && slot.getHourlyRate() != null) {
            rate = slot.getHourlyRate();
        } else if (pricingRepository != null && booking.getVehicle() != null
                && booking.getVehicle().getVehicleType() != null) {
            String vt = booking.getVehicle().getVehicleType().trim().toUpperCase();
            var pr = pricingRepository.findByVehicleType(vt);
            if (pr.isPresent() && pr.get().getHourlyRate() != null) {
                rate = pr.get().getHourlyRate();
            }
        }

        double total = calculateCost(booking.getCheckInTime(), now, rate);
        booking.setTotalCost(total);
        booking.setStatus(BookingStatus.CHECKED_OUT);

        Booking saved = bookingRepository.save(booking);

        // mark slot available
        if (slot != null) {
            slot.setStatus(SlotStatus.AVAILABLE);
            parkingSlotRepository.save(slot);
        }

        // optional: create Payment record (if repository exists)
        try {
            if (paymentRepository != null) {
                Payment payment = new Payment();
                payment.setBooking(saved);
                payment.setAmount(saved.getTotalCost());
                payment.setStatus(PaymentStatus.COMPLETED);
                payment.setPaymentTime(LocalDateTime.now());
                paymentRepository.save(payment);
                // set payment on booking for test and API
                saved.setPayment(payment);
                bookingRepository.save(saved);
            }
        } catch (Exception e) {
            // don't block checkout on payment record failure; log
            logger.warn("Failed to persist Payment for bookingId={}: {}", bookingId, e.getMessage());
        }

        logger.info("Checkout complete for bookingId={}, amount={}", bookingId, total);
        // Force initialization of all related entities to avoid Hibernate proxy
        // serialization issues
        if (saved.getSlot() != null) {
            saved.getSlot().getSlotNumber();
        }
        if (saved.getUser() != null) {
            saved.getUser().getUsername();
        }
        if (saved.getVehicle() != null) {
            saved.getVehicle().getVehicleNumber();
        }
        if (saved.getPayment() != null) {
            saved.getPayment().getPaymentMethod();
        }
        return saved;
    }

    @Override
    @Transactional
    public Booking checkIn(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));
        if (booking.getStatus() == BookingStatus.CHECKED_IN) {
            return booking;
        }
        if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.CHECKED_OUT) {
            throw new IllegalStateException("Cannot check-in booking in status: " + booking.getStatus());
        }
        booking.setCheckInTime(LocalDateTime.now());
        booking.setStatus(BookingStatus.CHECKED_IN);
        ParkingSlot slot = booking.getSlot();
        if (slot != null) {
            slot.setStatus(SlotStatus.OCCUPIED);
            parkingSlotRepository.save(slot);
        }
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Booking cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));
        if (booking.getStatus() == BookingStatus.CHECKED_IN) {
            throw new IllegalStateException("Cannot cancel a booking after check-in");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        ParkingSlot slot = booking.getSlot();
        if (slot != null) {
            slot.setStatus(SlotStatus.AVAILABLE);
            parkingSlotRepository.save(slot);
        }
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getUserBookings(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        return bookingRepository.findByUserWithDetails(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParkingSlot> getAvailableSlots() {
        return parkingSlotRepository.findByStatus(SlotStatus.AVAILABLE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParkingSlot> getAvailableSlotsByVehicleType(String vehicleType) {
        return parkingSlotRepository.findByStatusAndVehicleType(SlotStatus.AVAILABLE, vehicleType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getRecentBookings(String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        return bookingRepository.findTop5ByUserOrderByBookingTimeDesc(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Booking searchBooking(Long slotId) {
        return bookingRepository.findBySlot_SlotIdAndStatus(slotId, BookingStatus.CHECKED_IN)
                .orElseThrow(() -> new IllegalArgumentException("No active booking found for this slot"));
    }

    @Override
    @Transactional
    public Booking checkoutBySlot(Long slotId) {
        Booking booking = bookingRepository.findBySlot_SlotIdAndStatus(slotId, BookingStatus.CHECKED_IN)
                .orElseThrow(() -> new IllegalArgumentException("No active booking found for this slot"));

        ParkingSlot slot = booking.getSlot();
        if (slot != null) {
            slot.setStatus(SlotStatus.AVAILABLE);
            parkingSlotRepository.save(slot);
        }

        LocalDateTime now = LocalDateTime.now();
        booking.setCheckOutTime(now);
        // determine hourly rate using same precedence as checkOut(): slot.rate ->
        // pricing table by vehicle type -> default
        Double rate = defaultHourlyRate;
        if (slot != null && slot.getHourlyRate() != null) {
            rate = slot.getHourlyRate();
        } else if (pricingRepository != null && booking.getVehicle() != null
                && booking.getVehicle().getVehicleType() != null) {
            String vt = booking.getVehicle().getVehicleType().trim().toUpperCase();
            var pr = pricingRepository.findByVehicleType(vt);
            if (pr.isPresent() && pr.get().getHourlyRate() != null) {
                rate = pr.get().getHourlyRate();
            }
        }
        double total = calculateCost(booking.getCheckInTime(), now, rate);
        booking.setTotalCost(total);
        booking.setStatus(BookingStatus.CHECKED_OUT);
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Payment checkout(Long slotId) {
        Booking booking = checkoutBySlot(slotId);
        double amount = booking.getTotalCost() != null ? booking.getTotalCost() : 0.0;
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(amount);
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPaymentTime(LocalDateTime.now());
        if (paymentRepository != null) {
            paymentRepository.save(payment);
        }
        return payment;
    }

    @Override
    public double calculateCost(LocalDateTime checkIn, LocalDateTime checkOut, double hourlyRate) {
        if (checkIn == null || checkOut == null)
            return 0.0;
        Duration duration = Duration.between(checkIn, checkOut);
        long minutes = duration.toMinutes();
        // charge per hour, rounding up partial hours
        long hours = (minutes + 59) / 60;
        return hours * hourlyRate;
    }

    @Override
    @Transactional(readOnly = true)
    public Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));
    }

    @Override
    @Transactional
    public Booking bookSlot(Long userId, Long slotId, String vehicleNumber) {
        logger.info("bookSlot requested: userId={}, slotId={}, vehicleNumber={}", userId, slotId, vehicleNumber);
        ParkingSlot slot = parkingSlotRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Slot not found: " + slotId));
        if (slot.getStatus() != SlotStatus.AVAILABLE) {
            logger.warn("Attempt to book non-available slot: slotId={}, status={}", slotId, slot.getStatus());
            throw new IllegalStateException("Slot is not available");
        }

        // Resolve user (owner) - no fallback; require valid userId
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // create vehicle with required fields
        Vehicle v = new Vehicle();
        v.setVehicleNumber(vehicleNumber);
        v.setVehicleType(slot.getVehicleType() != null ? slot.getVehicleType() : "CAR");
        v.setOwner(owner);
        vehicleRepository.save(v);

        Booking booking = new Booking();
        booking.setSlot(slot);
        booking.setUser(owner);
        booking.setVehicle(v);
        booking.setBookingTime(LocalDateTime.now());
        booking.setCheckInTime(LocalDateTime.now());
        booking.setStatus(BookingStatus.CHECKED_IN);
        Booking saved = bookingRepository.save(booking);

        // mark slot occupied
        slot.setStatus(SlotStatus.OCCUPIED);
        parkingSlotRepository.save(slot);

        logger.info("bookSlot success: bookingId={}, slotId={}, userId={}", saved.getBookingId(), slotId,
                owner.getUserId());
        return saved;
    }

    // Convenience overload: resolve user by username
    @Override
    @Transactional
    public Booking bookSlotByUsername(Long slotId, String username, String vehicleNumber) {
        logger.info("bookSlot(byUsername) requested: username={}, slotId={}, vehicleNumber={}", username, slotId,
                vehicleNumber);
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        return bookSlot(user.getUserId(), slotId, vehicleNumber);
    }
}