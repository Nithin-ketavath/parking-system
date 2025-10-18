package com.apc.parking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;

import com.apc.parking.entity.Payment;
import com.apc.parking.entity.PaymentStatus;

import org.junit.jupiter.api.Test;

class PaymentEntityTest {
    @Test
    void testPaymentFields() {
        Payment payment = new Payment();
        payment.setAmount(100.0);
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPaymentTime(LocalDateTime.now());
        payment.setPaymentMethod("CASH");
        payment.setTransactionId("TXN123");

        assertEquals(100.0, payment.getAmount());
        assertEquals(PaymentStatus.COMPLETED, payment.getStatus());
        assertEquals("CASH", payment.getPaymentMethod());
        assertEquals("TXN123", payment.getTransactionId());
        assertNotNull(payment.getPaymentTime());
    }
}
