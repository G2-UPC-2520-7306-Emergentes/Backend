// application/outbound/hashing/HashingService.java
package com.foodchain.identity_context.application.outbound.hashing;

public interface HashingService {
    String encode(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}