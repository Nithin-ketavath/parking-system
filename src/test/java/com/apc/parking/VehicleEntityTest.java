package com.apc.parking;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.apc.parking.entity.Vehicle;

import org.junit.jupiter.api.Test;

class VehicleEntityTest {
    @Test
    void testVehicleFields() {
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleNumber("MH12AB1234");
        vehicle.setVehicleType("CAR");
        assertEquals("MH12AB1234", vehicle.getVehicleNumber());
        assertEquals("CAR", vehicle.getVehicleType());
    }
}
