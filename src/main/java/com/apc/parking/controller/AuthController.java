package com.apc.parking.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private com.apc.parking.security.JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            String role = userDetails.getAuthorities().iterator().next().getAuthority();
            String token = jwtUtil.generateToken(username, role);
            // also set token as HttpOnly cookie so browser sends it on subsequent requests
            jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("JWT", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge((int) (jwtUtil.getExpirationMs() / 1000L));
            org.springframework.web.context.request.RequestAttributes ra = org.springframework.web.context.request.RequestContextHolder
                    .currentRequestAttributes();
            if (ra instanceof org.springframework.web.context.request.ServletRequestAttributes) {
                jakarta.servlet.http.HttpServletResponse resp = ((org.springframework.web.context.request.ServletRequestAttributes) ra)
                        .getResponse();
                if (resp != null) {
                    resp.addCookie(cookie);
                }
            }
            return ResponseEntity.ok(Map.of("token", token));
        } catch (org.springframework.security.authentication.LockedException le) {
            // account is locked/blocked
            return ResponseEntity.status(423).body(
                    Map.of("error", "account_blocked", "message", "Your account has been blocked. Contact admin."));
        } catch (org.springframework.security.core.AuthenticationException ae) {
            return ResponseEntity.status(401).body(Map.of("error", "invalid_credentials"));
        }
    }
}
