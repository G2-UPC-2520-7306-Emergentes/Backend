// interfaces/rest/resources/RegisterStepResource.java
package com.foodchain.traceability_context.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import java.util.Date;

/**
 * DTO para la petición de registrar un nuevo evento de trazabilidad.
 * Contiene las validaciones de la capa de interfaz.
 */

public record RegisterStepResource(
        @NotNull UUID batchId,
        @NotBlank String eventType,
        @NotNull Double latitude,
        @NotNull Double longitude,
        Date clientCreatedAt // Campo opcional que el frontend enviará si es un borrador sincronizado
) {}