// identity-context/interfaces/rest/resources/UserDetailsResource.java
package com.foodchain.identity_context.interfaces.rest.resources;

import java.util.Set;
import java.util.UUID;

// Este DTO representa los detalles que otros servicios necesitan saber sobre un usuario.
public record UserDetailsResource(UUID userId, String email, UUID enterpriseId, Set<String> roles) {}