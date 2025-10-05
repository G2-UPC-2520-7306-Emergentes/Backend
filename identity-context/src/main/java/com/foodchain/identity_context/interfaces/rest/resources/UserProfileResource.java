// interfaces/rest/resources/UserProfileResource.java
package com.foodchain.identity_context.interfaces.rest.resources;

import java.util.UUID;

public record UserProfileResource(UUID id, String email, UUID enterpriseId) {}