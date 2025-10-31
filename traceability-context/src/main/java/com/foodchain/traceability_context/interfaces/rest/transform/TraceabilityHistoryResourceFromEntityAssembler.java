// interfaces/rest/transform/TraceabilityHistoryResourceFromEntityAssembler.java
package com.foodchain.traceability_context.interfaces.rest.transform;

import com.foodchain.traceability_context.domain.model.entities.TraceabilityEvent;
import com.foodchain.traceability_context.interfaces.rest.resources.TraceabilityEventResource;
import com.foodchain.traceability_context.interfaces.rest.resources.TraceabilityHistoryResource;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TraceabilityHistoryResourceFromEntityAssembler {
    public static TraceabilityHistoryResource toResourceFromEntities(UUID batchId, List<TraceabilityEvent> events) {

        var eventResources = events.stream().map(event -> new TraceabilityEventResource(
                event.getId(),
                event.getEventType(),
                event.getEventDate(),
                event.getActorId(),
                event.getLocation().getLatitude(),
                event.getLocation().getLongitude(),
                event.getBlockchainStatus().name(), // Convertimos el enum a String
                event.getTransactionHash()
        )).collect(Collectors.toList());

        return new TraceabilityHistoryResource(batchId, eventResources);
    }
}