
package com.apc.parking.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtUtil jwtUtil) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/login",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/",
                                "/signup",
                                "/error",
                                "/profile",
                                "/login",
                                "/booking",
                                "/checkout",
                                "/api/slots/stats",
                                "/api/slots/available",
                                "/api/bookings/active")
                        .permitAll()
                        // admin endpoints and page must be limited to ADMIN role
                        .requestMatchers("/admin", "/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                // add JwtAuthenticationFilter with injected JwtUtil
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(sm -> sm.sessionCreationPolicy(
                        org.springframework.security.config.http.SessionCreationPolicy.STATELESS));
        // configure logout to clear JWT cookie (stateless JWT logout)
        http.logout(logout -> logout
                .logoutUrl("/logout")
                .addLogoutHandler((request, response, authentication) -> {
                    Cookie cookie = new Cookie("JWT", "");
                    cookie.setHttpOnly(true);
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    // Optionally set sameSite and secure when needed
                    response.addCookie(cookie);
                })
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setStatus(HttpServletResponse.SC_OK);
                }));
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }

}
