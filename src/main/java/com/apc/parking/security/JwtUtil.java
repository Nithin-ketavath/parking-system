package com.apc.parking.security;

import java.util.Base64;
import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final long expirationMs;

    // Use relaxed property placeholders with defaults so tests that don't define
    // jwt.* won't fail
    public JwtUtil(@Value("${jwt.secret:}") String base64Secret, @Value("${jwt.expirationMs:0}") long expirationMs) {
        // If no secret provided, use a short-lived development default (not for
        // production)
        if (base64Secret == null || base64Secret.isEmpty()) {
            base64Secret = "dev-default-secret-please-change";
        }

        byte[] secretBytes;
        try {
            secretBytes = Base64.getDecoder().decode(base64Secret);
        } catch (IllegalArgumentException e) {
            secretBytes = base64Secret.getBytes();
        }

        // If expiration not provided or zero, default to 24 hours
        if (expirationMs <= 0) {
            expirationMs = 24 * 60 * 60 * 1000L;
        }

        this.algorithm = Algorithm.HMAC256(secretBytes);
        this.verifier = JWT.require(this.algorithm).build();
        this.expirationMs = expirationMs;
    }

    public String generateToken(String username, String role) {
        long now = System.currentTimeMillis();
        return JWT.create()
                .withSubject(username)
                .withClaim("role", role)
                .withIssuedAt(new Date(now))
                .withExpiresAt(new Date(now + expirationMs))
                .sign(algorithm);
    }

    public DecodedJWT validateToken(String token) {
        return verifier.verify(token);
    }

    public long getExpirationMs() {
        return expirationMs;
    }
}
