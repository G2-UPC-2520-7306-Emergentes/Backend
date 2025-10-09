// interfaces/rest/resources/RegisterStepResource.java
package com.foodchain.traceability_context.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * DTO para la petición de registrar un nuevo evento de trazabilidad.
 * Contiene las validaciones de la capa de interfaz.
 */
public record RegisterStepResource(
        @NotNull UUID batchId,
        @NotBlank String eventType,
        @NotNull Double latitude,
        @NotNull Double longitude
        // El actorId no lo pedimos en el cuerpo, lo obtendremos del token JWT
) {}