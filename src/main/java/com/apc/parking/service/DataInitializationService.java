package com.apc.parking.service;

import com.apc.parking.entity.*;
import com.apc.parking.repository.ParkingSlotRepository;
import com.apc.parking.repository.UserRepository;
import com.apc.parking.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class DataInitializationService implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ParkingSlotRepository slotRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initializeUsers();
        initializeParkingSlots();
    }

    private void initializeUsers() {
        if (userRepository.count() == 0) {
            // Admin user
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@parking.com");
            admin.setFullName("System Administrator");
            admin.setPhoneNumber("9876543210");
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);

            // Vehicles for admin
            Vehicle adminCar = new Vehicle();
            adminCar.setVehicleNumber("ADMIN123");
            adminCar.setVehicleType("CAR");
            adminCar.setOwner(admin);
            vehicleRepository.save(adminCar);

            // User 1
            User user1 = new User();
            user1.setUsername("john_doe");
            user1.setPassword(passwordEncoder.encode("password123"));
            user1.setEmail("john@example.com");
            user1.setFullName("John Doe");
            user1.setPhoneNumber("9876543211");
            user1.setRole(Role.USER);
            userRepository.save(user1);

            Vehicle johnCar = new Vehicle();
            johnCar.setVehicleNumber("JOHN001");
            johnCar.setVehicleType("CAR");
            johnCar.setOwner(user1);
            vehicleRepository.save(johnCar);

            // User 2
            User user2 = new User();
            user2.setUsername("jane_smith");
            user2.setPassword(passwordEncoder.encode("password123"));
            user2.setEmail("jane@example.com");
            user2.setFullName("Jane Smith");
            user2.setPhoneNumber("9876543212");
            user2.setRole(Role.USER);
            userRepository.save(user2);

            Vehicle janeBike = new Vehicle();
            janeBike.setVehicleNumber("JANE001");
            janeBike.setVehicleType("BIKE");
            janeBike.setOwner(user2);
            vehicleRepository.save(janeBike);

            System.out.println("Sample users and vehicles created successfully!");
        }
    }

    private void initializeParkingSlots() {
        if (slotRepository.count() == 0) {
            // Create car parking slots
            for (int i = 1; i <= 20; i++) {
                ParkingSlot slot = new ParkingSlot();
                slot.setSlotNumber("C" + String.format("%03d", i));
                slot.setLocation("Ground Floor - Car Section");
                slot.setStatus(SlotStatus.AVAILABLE);
                slot.setHourlyRate(50.0);
                slot.setVehicleType("CAR");
                slotRepository.save(slot);
            }

            // Bike slots
            for (int i = 1; i <= 15; i++) {
                ParkingSlot slot = new ParkingSlot();
                slot.setSlotNumber("B" + String.format("%03d", i));
                slot.setLocation("Ground Floor - Bike Section");
                slot.setStatus(SlotStatus.AVAILABLE);
                slot.setHourlyRate(25.0);
                slot.setVehicleType("BIKE");
                slotRepository.save(slot);
            }

            // Truck slots
            for (int i = 1; i <= 5; i++) {
                ParkingSlot slot = new ParkingSlot();
                slot.setSlotNumber("T" + String.format("%03d", i));
                slot.setLocation("Basement - Truck Section");
                slot.setStatus(SlotStatus.AVAILABLE);
                slot.setHourlyRate(100.0);
                slot.setVehicleType("TRUCK");
                slotRepository.save(slot);
            }

            System.out.println("Sample parking slots created successfully!");
        }
    }
}

