// infrastructure/outbound/tokens/JwtTokenServiceImpl.java
package com.foodchain.identity_context.application.outbound.tokens;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtTokenServiceImpl implements TokenService {

    private final SecretKey key;
    private final long expirationInMs;

    public JwtTokenServiceImpl(
            @Value("${authorization.jwt.secret}") String secret,
            @Value("${authorization.jwt.expiration.days}") int expirationDays) {

        if (secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 256 bits (32 bytes).");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationInMs = (long) expirationDays * 24 * 60 * 60 * 1000;
    }

    @Override
    public String generateToken(String email) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expirationInMs);

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(key)
                .compact();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            // Log the exception (e.g., ExpiredJwtException, MalformedJwtException)
            return false;
        }
    }

    @Override
    public String getEmailFromToken(String token) {
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload().getSubject();
    }
}