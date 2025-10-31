package com.foodchain.shared_domain.domain.model.aggregates;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

public record UserDetails(
        UUID userId,
        String email,
        UUID enterpriseId,
        Set<String> roles
) implements Serializable {}