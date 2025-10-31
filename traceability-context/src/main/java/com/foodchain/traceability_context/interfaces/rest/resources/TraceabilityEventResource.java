// interfaces/rest/resources/TraceabilityEventResource.java
package com.foodchain.traceability_context.interfaces.rest.resources;

import java.util.Date;
import java.util.UUID;

/**
 * DTO que representa un único evento de trazabilidad para la respuesta de la API.
 */
public record TraceabilityEventResource(
        UUID eventId,
        String eventType,
        Date eventDate,
        UUID actorId,
        Double latitude,
        Double longitude,
        String blockchainStatus,
        String transactionHash
) {}
