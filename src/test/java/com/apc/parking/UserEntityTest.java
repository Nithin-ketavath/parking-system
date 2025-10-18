package com.apc.parking;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.apc.parking.entity.Role;
import com.apc.parking.entity.User;

import org.junit.jupiter.api.Test;

class UserEntityTest {
    @Test
    void testUserFields() {
        User user = new User();
        user.setUsername("john");
        user.setPassword("pass");
        user.setEmail("john@example.com");
        user.setFullName("John Doe");
        user.setPhoneNumber("9876543210");
        user.setRole(Role.USER);

        assertEquals("john", user.getUsername());
        assertEquals("pass", user.getPassword());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("John Doe", user.getFullName());
        assertEquals("9876543210", user.getPhoneNumber());
        assertEquals(Role.USER, user.getRole());
    }
}
