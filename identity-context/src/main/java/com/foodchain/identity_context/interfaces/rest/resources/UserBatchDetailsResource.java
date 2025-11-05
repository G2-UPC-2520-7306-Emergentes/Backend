// interfaces/rest/resources/UserBatchDetailsResource.java
package com.foodchain.identity_context.interfaces.rest.resources;
import java.util.UUID;

// Un DTO simple con la información que otros servicios necesitan
public record UserBatchDetailsResource(UUID id, String email) {}