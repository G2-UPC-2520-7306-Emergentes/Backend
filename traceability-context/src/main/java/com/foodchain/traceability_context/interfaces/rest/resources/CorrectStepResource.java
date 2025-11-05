// interfaces/rest/resources/CorrectStepResource.java
package com.foodchain.traceability_context.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CorrectStepResource(
        @NotBlank String justification,
        @NotNull Double latitude,
        @NotNull Double longitude
) {}