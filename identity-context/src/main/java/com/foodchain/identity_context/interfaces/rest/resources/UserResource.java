// interfaces/rest/resources/UserResource.java
package com.foodchain.identity_context.interfaces.rest.resources;

import java.util.Set;
import java.util.UUID;

/**
 * DTO que representa la información de un usuario para ser expuesta en la API.
 */
public record UserResource(
        UUID id,
        String email,
        UUID enterpriseId,
        Set<String> roles
) {}