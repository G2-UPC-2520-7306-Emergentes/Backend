// EN: identity-context/interfaces/rest/resources/UserBatchDetailsResource.java
package com.foodchain.identity_context.interfaces.rest.resources;

import java.util.Set;
import java.util.UUID;

// Este DTO ahora contiene toda la información necesaria
public record UserBatchDetailsResource(
        UUID userId,
        String email,
        UUID enterpriseId,
        Set<String> roles
) {}