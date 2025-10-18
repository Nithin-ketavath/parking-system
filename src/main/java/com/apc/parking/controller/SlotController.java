package com.apc.parking.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.apc.parking.entity.ParkingSlot;
import com.apc.parking.entity.SlotStatus;
import com.apc.parking.repository.ParkingSlotRepository;
import com.apc.parking.repository.PricingRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/slots")
@CrossOrigin(origins = "*")
public class SlotController {
    private final ParkingSlotRepository slotRepository;
    @Autowired(required = false)
    private PricingRepository pricingRepository;

    @Value("${parking.defaultHourlyRate:50.0}")
    private double defaultHourlyRate;

    public SlotController(ParkingSlotRepository slotRepository) {
        this.slotRepository = slotRepository;
    }

    @GetMapping
    public ResponseEntity<List<ParkingSlot>> getAllSlots() {
        List<ParkingSlot> slots = slotRepository.findAll();
        slots.forEach(s -> s.setHourlyRate(computeEffectiveRate(s)));
        return ResponseEntity.ok(slots);
    }

    @GetMapping("/{slotId:\\d+}")
    public ResponseEntity<ParkingSlot> getSlotById(@PathVariable Long slotId) {
        Optional<ParkingSlot> slot = slotRepository.findById(slotId);
        return slot.map(s -> {
            s.setHourlyRate(computeEffectiveRate(s));
            return ResponseEntity.ok(s);
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ParkingSlot>> getSlotsByStatus(@PathVariable SlotStatus status) {
        List<ParkingSlot> slots = slotRepository.findByStatus(status);
        slots.forEach(s -> s.setHourlyRate(computeEffectiveRate(s)));
        return ResponseEntity.ok(slots);
    }

    @GetMapping("/vehicle-type/{vehicleType}")
    public ResponseEntity<List<ParkingSlot>> getSlotsByVehicleType(@PathVariable String vehicleType) {
        List<ParkingSlot> slots = slotRepository.findByVehicleType(vehicleType);
        return ResponseEntity.ok(slots);
    }

    @GetMapping("/available")
    public ResponseEntity<List<ParkingSlot>> getAvailableSlots() {
        List<ParkingSlot> slots = slotRepository.findByStatus(SlotStatus.AVAILABLE);
        slots.forEach(s -> s.setHourlyRate(computeEffectiveRate(s)));
        return ResponseEntity.ok(slots);
    }

    @GetMapping("/available/{vehicleType}")
    public ResponseEntity<List<ParkingSlot>> getAvailableSlotsByVehicleType(@PathVariable String vehicleType) {
        List<ParkingSlot> slots = slotRepository.findByStatusAndVehicleType(SlotStatus.AVAILABLE, vehicleType);
        slots.forEach(s -> s.setHourlyRate(computeEffectiveRate(s)));
        return ResponseEntity.ok(slots);
    }

    private double computeEffectiveRate(ParkingSlot slot) {
        if (slot == null)
            return defaultHourlyRate;
        // Prefer admin-set pricing if present; otherwise fall back to slot.hourlyRate,
        // then default
        if (pricingRepository != null && slot.getVehicleType() != null) {
            String vt = slot.getVehicleType().trim().toUpperCase();
            var pr = pricingRepository.findByVehicleType(vt);
            if (pr.isPresent() && pr.get().getHourlyRate() != null)
                return pr.get().getHourlyRate();
        }
        if (slot.getHourlyRate() != null)
            return slot.getHourlyRate();
        return defaultHourlyRate;
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Integer>> getSlotStats() {
        List<ParkingSlot> all = slotRepository.findAll();
        int total = all.size();
        int available = (int) all.stream().filter(s -> s.getStatus() == SlotStatus.AVAILABLE).count();
        int occupied = total - available; // includes BOOKED + OCCUPIED + others
        return ResponseEntity.ok(Map.of(
                "total", total,
                "available", available,
                "occupied", occupied));
    }

    @PostMapping
    public ResponseEntity<ParkingSlot> createSlot(@RequestBody ParkingSlot slot) {
        try {
            ParkingSlot savedSlot = slotRepository.save(slot);
            return ResponseEntity.ok(savedSlot);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{slotId}")
    public ResponseEntity<ParkingSlot> updateSlot(@PathVariable Long slotId, @RequestBody ParkingSlot slot) {
        try {
            if (!slotRepository.existsById(slotId)) {
                return ResponseEntity.notFound().build();
            }
            slot.setSlotId(slotId);
            ParkingSlot updatedSlot = slotRepository.save(slot);
            return ResponseEntity.ok(updatedSlot);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{slotId}")
    public ResponseEntity<Void> deleteSlot(@PathVariable Long slotId) {
        try {
            if (!slotRepository.existsById(slotId)) {
                return ResponseEntity.notFound().build();
            }
            slotRepository.deleteById(slotId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
