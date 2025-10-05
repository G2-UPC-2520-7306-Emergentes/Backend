// interfaces/rest/resources/SignUpResource.java
package com.foodchain.identity_context.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record SignUpResource(
        @NotNull UUID enterpriseId,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 30) String password
) {}