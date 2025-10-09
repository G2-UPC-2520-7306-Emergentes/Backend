// application/outbound/iam/UserDetails.java
package com.foodchain.traceability_context.application.outbound.iam;

import java.util.Set;
import java.util.UUID;

public record UserDetails(UUID userId, String email, UUID enterpriseId, Set<String> roles) {}
