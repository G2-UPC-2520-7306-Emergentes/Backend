// infrastructure/outbound/tokens/JwtTokenServiceImpl.java
package com.foodchain.identity_context.application.outbound.tokens;


import com.foodchain.identity_context.domain.model.entities.InvalidatedToken;
import com.foodchain.identity_context.domain.repositories.InvalidatedTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
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
    private final InvalidatedTokenRepository invalidatedTokenRepository;


    public JwtTokenServiceImpl(
            @Value("${authorization.jwt.secret}") String secret,
            @Value("${authorization.jwt.expiration.days}") int expirationDays,
            InvalidatedTokenRepository invalidatedTokenRepository) {

        if (secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 256 bits (32 bytes).");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationInMs = (long) expirationDays * 24 * 60 * 60 * 1000;
        this.invalidatedTokenRepository = invalidatedTokenRepository;
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

    @Override
    public void invalidateToken(String token) {
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        String tokenId = claims.getId(); // Obtenemos el JTI
        Date expiryDate = claims.getExpiration();
        invalidatedTokenRepository.save(new InvalidatedToken(tokenId, expiryDate));
    }

    @Override
    public boolean isTokenInvalidated(String token) {
        try {
            String tokenId = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getId();
            return invalidatedTokenRepository.findByTokenId(tokenId).isPresent();
        } catch (JwtException e) {
            // Si el token es inválido (expirado, malformado), lo consideramos invalidado.
            return true;
        }
    }
}