package com.coworking.bookingservice.jwtUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {
    @Value("${jwt.secret}")
    private String secret;

    public Claims extractClaims(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(
                Arrays.copyOf(secret.getBytes(StandardCharsets.UTF_8), 32)
            );
            return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        } catch (JwtException | IllegalArgumentException e){
            throw new RuntimeException("JWT validation failed: " + e.getMessage(), e);
        }
    }
}