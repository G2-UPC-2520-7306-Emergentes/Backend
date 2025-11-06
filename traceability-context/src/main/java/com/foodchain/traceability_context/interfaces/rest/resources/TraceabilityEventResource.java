// interfaces/rest/resources/TraceabilityEventResource.java
package com.foodchain.traceability_context.interfaces.rest.resources;

import java.util.Date;
import java.util.UUID;

/**
 * DTO para representar un evento de trazabilidad en las respuestas de la API.
 */
public record TraceabilityEventResource(
        UUID id,
        UUID batchId,
        String eventType,
        Date eventDate,
        UUID actorId,
        String actorName,
        LocationResource location,
        String blockchainStatus,
        String transactionHash, // Será nulo al principio
        String proofImageUrl,
        String proofImageHash,
        String verificationUrl
) {
    /**
     * DTO anidado para la ubicación.
     */
    public record LocationResource(
            Double latitude,
            Double longitude,
            String address,
            String city,
            String region,
            String country
    ) {}
}