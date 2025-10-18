package com.apc.parking;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.apc.parking.entity.ParkingSlot;
import com.apc.parking.entity.SlotStatus;

import org.junit.jupiter.api.Test;

class ParkingSlotEntityTest {
    @Test
    void testParkingSlotFields() {
        ParkingSlot slot = new ParkingSlot();
        slot.setSlotNumber("A1");
        slot.setLocation("Basement");
        slot.setVehicleType("CAR");
        slot.setHourlyRate(50.0);
        slot.setStatus(SlotStatus.AVAILABLE);

        assertEquals("A1", slot.getSlotNumber());
        assertEquals("Basement", slot.getLocation());
        assertEquals("CAR", slot.getVehicleType());
        assertEquals(50.0, slot.getHourlyRate());
        assertEquals(SlotStatus.AVAILABLE, slot.getStatus());
    }
}
