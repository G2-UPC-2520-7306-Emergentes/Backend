// interfaces/rest/resources/TraceabilityHistoryResource.java
package com.foodchain.traceability_context.interfaces.rest.resources;

import java.util.List;
import java.util.UUID;

/**
 * DTO que representa el historial completo de un lote.
 */
public record TraceabilityHistoryResource(
        UUID batchId,
        List<TraceabilityEventResource> events
) {}