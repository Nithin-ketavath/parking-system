package com.apc.parking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.apc.parking")
@EnableJpaRepositories(basePackages = "com.apc.parking.repository")
@EntityScan(basePackages = "com.apc.parking.entity")
public class ParkingManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(ParkingManagementApplication.class, args);
    }
}


