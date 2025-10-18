package com.apc.parking.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.apc.parking.entity.Booking;
import com.apc.parking.entity.BookingStatus;
import com.apc.parking.entity.ParkingSlot;
import com.apc.parking.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser(User user);

    List<Booking> findBySlot(ParkingSlot slot);

    List<Booking> findByStatus(BookingStatus status);

    List<Booking> findByUserAndStatus(User user, BookingStatus status);

    List<Booking> findByUserAndStatusIn(User user, java.util.List<BookingStatus> statuses);

    @Query("SELECT b FROM Booking b JOIN FETCH b.slot JOIN FETCH b.vehicle JOIN FETCH b.user WHERE b.user = :user AND b.status IN :statuses")
    List<Booking> findByUserAndStatusInWithDetails(User user, java.util.List<BookingStatus> statuses);

    List<Booking> findByCheckInTimeBetween(LocalDateTime start, LocalDateTime end);

    List<Booking> findByCheckOutTimeBetween(LocalDateTime start, LocalDateTime end);

    Optional<Booking> findByUserAndStatusAndSlot(User user, BookingStatus status, ParkingSlot slot);

    @Query("SELECT b FROM Booking b JOIN FETCH b.slot JOIN FETCH b.vehicle JOIN FETCH b.user WHERE b.status = :status")
    List<Booking> findByStatusWithDetails(BookingStatus status);

    @Query("SELECT b FROM Booking b JOIN FETCH b.slot JOIN FETCH b.vehicle WHERE b.user = :user")
    List<Booking> findByUserWithDetails(User user);

    List<Booking> findTop5ByUserOrderByBookingTimeDesc(User user);

    java.util.Optional<Booking> findBySlot_SlotIdAndStatus(Long slotId, com.apc.parking.entity.BookingStatus status);
}
