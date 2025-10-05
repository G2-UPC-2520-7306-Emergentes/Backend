// application/outbound/tokens/TokenService.java
package com.foodchain.identity_context.application.outbound.tokens;

public interface TokenService {
    String generateToken(String email);
    boolean validateToken(String token);
    String getEmailFromToken(String token);
}