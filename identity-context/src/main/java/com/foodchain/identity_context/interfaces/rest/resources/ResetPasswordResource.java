package com.foodchain.identity_context.interfaces.rest.resources;

public record ResetPasswordResource(
        @jakarta.validation.constraints.NotBlank String token,
        @jakarta.validation.constraints.Size(min = 8, max = 30) String newPassword
) {}