// interfaces/rest/resources/AssignRoleResource.java
package com.foodchain.identity_context.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

public record AssignRoleResource(@NotBlank String roleName) {}