package com.apc.parking.controller;

import com.apc.parking.entity.Role;
import com.apc.parking.entity.User;
import com.apc.parking.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class SignupController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SignupController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/signup")
    public String showSignup(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String handleSignup(@ModelAttribute("user") User formUser, Model model) {
        // Basic uniqueness check
        if (formUser.getUsername() == null || formUser.getUsername().isBlank()) {
            model.addAttribute("error", "Username is required");
            return "signup";
        }
        if (userRepository.findByUsername(formUser.getUsername()).isPresent()) {
            model.addAttribute("error", "Username already exists");
            return "signup";
        }

        // Encode password and set defaults
        formUser.setPassword(passwordEncoder.encode(formUser.getPassword()));
        formUser.setRole(Role.USER);

        userRepository.save(formUser);
        return "redirect:/login";
    }
}




