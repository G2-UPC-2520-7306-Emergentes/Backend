// interfaces/rest/resources/AuthenticatedUserResource.java
package com.foodchain.identity_context.interfaces.rest.resources;

public record AuthenticatedUserResource(String email, String token) {}