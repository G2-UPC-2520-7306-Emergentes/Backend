// interfaces/rest/resources/RoutePointResource.java
package com.foodchain.traceability_context.interfaces.rest.resources;

import java.util.Date;
import java.util.UUID;

/**
 * DTO ligero para representar un punto geográfico en la ruta de un lote.
 * Contiene solo la información necesaria para el mapa interactivo.
 */
public record RoutePointResource(
        UUID eventId,
        String eventType,
        Date eventDate,
        String actorName,
        Double latitude,
        Double longitude,
        String address
) {}