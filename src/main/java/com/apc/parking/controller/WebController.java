package com.apc.parking.controller;

import com.apc.parking.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String index(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            userRepository.findByUsername(username).ifPresent(user -> {
                model.addAttribute("username", user.getUsername());
                model.addAttribute("role", user.getRole());
            });
        } else {
            model.addAttribute("username", "Guest");
            model.addAttribute("role", "GUEST");
        }
        return "index";
    }

    @GetMapping("/booking")
    public String booking(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            userRepository.findByUsername(username).ifPresent(user -> {
                model.addAttribute("username", user.getUsername());
                model.addAttribute("role", user.getRole());
            });
        } else {
            model.addAttribute("username", "Guest");
            model.addAttribute("role", "GUEST");
        }
        return "booking";
    }

    @GetMapping("/checkout")
    public String checkout(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            userRepository.findByUsername(username).ifPresent(user -> {
                model.addAttribute("username", user.getUsername());
                model.addAttribute("role", user.getRole());
            });
        } else {
            model.addAttribute("username", "Guest");
            model.addAttribute("role", "GUEST");
        }
        return "checkout";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        String username = authentication.getName();
        userRepository.findByUsername(username).ifPresent(user -> model.addAttribute("user", user));
        return "profile";
    }

    @GetMapping("/admin")
    public String admin(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        // only admins should see this page; security will enforce role but we add a
        // simple guard
        if (!authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().contains("ADMIN"))) {
            return "error";
        }
        return "admin";
    }

    @GetMapping("/signout")
    public String signout() {
        return "signout";
    }
}
