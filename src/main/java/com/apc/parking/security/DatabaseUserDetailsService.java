package com.apc.parking.security;

import java.util.Collections;

import com.apc.parking.entity.Role;
import com.apc.parking.repository.UserRepository;

import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Primary
public class DatabaseUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public DatabaseUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.apc.parking.entity.User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        String roleName = user.getRole() == null ? Role.USER.name() : user.getRole().name();
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + roleName);

        // map blocked flag to accountLocked so Spring Security will fail authentication
        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(Collections.singleton(authority))
                .accountLocked(user.isBlocked())
                .build();
    }
}
