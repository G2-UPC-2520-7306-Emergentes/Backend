package com.foodchain.traceability_context.interfaces.rest.transform;

import com.foodchain.traceability_context.interfaces.rest.resources.TraceabilityEventResource;

public class TraceabilityEventResourceFromEntityAssembler {
    public static TraceabilityEventResource toResourceFromEntity(com.foodchain.traceability_context.domain.model.entities.TraceabilityEvent event) {
        return new TraceabilityEventResource(
                event.getId(),
                event.getEventType(),
                event.getEventDate(),
                event.getActorId(),
                event.getLocation().getLatitude(),
                event.getLocation().getLongitude(),
                event.getBlockchainStatus().name(), // Convertimos el enum a String
                event.getTransactionHash()
        );
    }
}
