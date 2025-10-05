// infrastructure/outbound/hashing/BcryptHashingServiceImpl.java
package com.foodchain.identity_context.infrastructure.outbound.hashing;

import com.foodchain.identity_context.application.outbound.hashing.HashingService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class BcryptHashingServiceImpl implements HashingService {
    private final BCryptPasswordEncoder encoder;

    public BcryptHashingServiceImpl() {
        this.encoder = new BCryptPasswordEncoder();
    }

    @Override
    public String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}