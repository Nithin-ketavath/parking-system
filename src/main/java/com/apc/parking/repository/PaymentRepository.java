package com.apc.parking.repository;

import com.apc.parking.entity.Payment;
import com.apc.parking.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByStatus(PaymentStatus status);
    List<Payment> findByPaymentTimeBetween(LocalDateTime start, LocalDateTime end);
    Optional<Payment> findByTransactionId(String transactionId);
    List<Payment> findByPaymentMethod(String paymentMethod);
}


