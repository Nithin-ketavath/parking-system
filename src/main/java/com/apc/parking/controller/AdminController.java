package com.apc.parking.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.apc.parking.entity.Pricing;
import com.apc.parking.entity.Role;
import com.apc.parking.entity.User;
import com.apc.parking.repository.PricingRepository;
import com.apc.parking.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PricingRepository pricingRepository;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> listUsers() {
        // filter out non-admin test/example users so they do not appear in the admin UI
        var all = userRepository.findAll();
        var filtered = all.stream().filter(u -> {
            if (u.getRole() != null && u.getRole().name().equals("ADMIN"))
                return true;
            String email = u.getEmail() == null ? "" : u.getEmail().toLowerCase();
            String username = u.getUsername() == null ? "" : u.getUsername().toLowerCase();
            if (email.endsWith("@example.com"))
                return false;
            if (username.startsWith("test"))
                return false;
            if (username.contains("john") || username.contains("jane"))
                return false;
            return true;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(filtered);
    }

    @PostMapping("/users/{id}/block")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> blockUser(@PathVariable Long id, @RequestParam boolean block) {
        return userRepository.findById(id).map(u -> {
            // Prevent blocking other admins or self-blocking
            // Fetch current authenticated user id from security context if available
            try {
                var auth = org.springframework.security.core.context.SecurityContextHolder.getContext()
                        .getAuthentication();
                if (auth != null && auth.isAuthenticated()
                        && auth.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
                    org.springframework.security.core.userdetails.User current = (org.springframework.security.core.userdetails.User) auth
                            .getPrincipal();
                    // current.getUsername() corresponds to our User.username
                    if (current.getUsername().equals(u.getUsername())) {
                        return ResponseEntity.badRequest().body(
                                Map.of("error", "cannot_block_self", "message", "Admins cannot block themselves"));
                    }
                }
            } catch (Exception ex) {
                // ignore and continue; fallback to role check below
            }
            if (u.getRole() == Role.ADMIN) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "cannot_block_admin", "message", "Cannot block another admin"));
            }
            u.setBlocked(block);
            userRepository.save(u);
            return ResponseEntity.ok(Map.of("userId", u.getUserId(), "blocked", u.isBlocked()));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return userRepository.findById(id).map(u -> {
            // Prevent deleting self or other admins
            try {
                var auth = org.springframework.security.core.context.SecurityContextHolder.getContext()
                        .getAuthentication();
                if (auth != null && auth.isAuthenticated()
                        && auth.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
                    org.springframework.security.core.userdetails.User current = (org.springframework.security.core.userdetails.User) auth
                            .getPrincipal();
                    if (current.getUsername().equals(u.getUsername())) {
                        return ResponseEntity.badRequest().body(
                                Map.of("error", "cannot_delete_self", "message", "Admins cannot delete themselves"));
                    }
                }
            } catch (Exception ex) {
                // ignore
            }
            if (u.getRole() == Role.ADMIN) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "cannot_delete_admin", "message", "Cannot delete another admin"));
            }
            userRepository.delete(u);
            return ResponseEntity.ok(Map.of("deletedUserId", id));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/pricing")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Pricing>> listPricing() {
        return ResponseEntity.ok(pricingRepository.findAll());
    }

    // Accepts payload like {"CAR":50.0, "BIKE":25.0}
    @PostMapping("/pricing")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> setPricing(@RequestBody Map<String, Double> rates) {
        for (var entry : rates.entrySet()) {
            String type = entry.getKey() == null ? "" : entry.getKey().trim().toUpperCase();
            Double rate = entry.getValue();
            pricingRepository.findByVehicleType(type).ifPresentOrElse(p -> {
                p.setHourlyRate(rate);
                pricingRepository.save(p);
            }, () -> {
                Pricing p = new Pricing(type, rate);
                // ensure vehicleType stored normalized
                p.setVehicleType(type);
                pricingRepository.save(p);
            });
        }
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    @PostMapping("/cleanup-test-users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> cleanupTestUsers() {
        List<com.apc.parking.entity.User> all = userRepository.findAll();
        List<Long> deletedIds = new ArrayList<>();
        List<com.apc.parking.entity.User> toDelete = new ArrayList<>();
        for (var u : all) {
            if (u.getRole() == Role.ADMIN)
                continue; // never delete admins
            String email = u.getEmail() == null ? "" : u.getEmail().toLowerCase();
            String username = u.getUsername() == null ? "" : u.getUsername().toLowerCase();
            // criteria: example.com email or obvious test usernames
            if (email.endsWith("@example.com") || username.startsWith("test") || username.contains("john")
                    || username.contains("jane")) {
                toDelete.add(u);
                deletedIds.add(u.getUserId());
            }
        }
        if (!toDelete.isEmpty()) {
            userRepository.deleteAll(toDelete);
        }
        return ResponseEntity.ok(Map.of("deletedCount", deletedIds.size(), "deletedIds", deletedIds));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> currentAdmin() {
        try {
            var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                Object principal = auth.getPrincipal();
                String username = null;
                String role = "";
                if (principal instanceof org.springframework.security.core.userdetails.User) {
                    org.springframework.security.core.userdetails.User u = (org.springframework.security.core.userdetails.User) principal;
                    username = u.getUsername();
                    var authorities = u.getAuthorities();
                    if (authorities != null && authorities.stream().anyMatch(a -> a.getAuthority().contains("ADMIN")))
                        role = "ADMIN";
                    else if (authorities != null
                            && authorities.stream().anyMatch(a -> a.getAuthority().contains("USER")))
                        role = "USER";
                }
                return ResponseEntity.ok(Map.of("username", username, "role", role));
            }
        } catch (Exception ex) {
            // ignore
        }
        return ResponseEntity.status(401).build();
    }
}
