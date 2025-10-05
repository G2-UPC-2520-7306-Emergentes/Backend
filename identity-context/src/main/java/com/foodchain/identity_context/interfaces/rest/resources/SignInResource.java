// interfaces/rest/resources/SignInResource.java
package com.foodchain.identity_context.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

public record SignInResource(@NotBlank String email, @NotBlank String password) {}