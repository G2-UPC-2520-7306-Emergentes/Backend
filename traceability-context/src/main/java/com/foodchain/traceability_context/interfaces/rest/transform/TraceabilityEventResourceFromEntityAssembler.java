// EN: traceability-context/src/main/java/com/foodchain/traceability_context/interfaces/rest/transform/TraceabilityEventResourceFromEntityAssembler.java
package com.foodchain.traceability_context.interfaces.rest.transform;

import com.foodchain.traceability_context.domain.model.entities.TraceabilityEvent;
import com.foodchain.traceability_context.interfaces.rest.resources.TraceabilityEventResource;

public class TraceabilityEventResourceFromEntityAssembler {

    /**
     * Método de mapeo simple.
     * Usado cuando el nombre del actor aún no ha sido resuelto.
     */
    public static TraceabilityEventResource toResourceFromEntity(TraceabilityEvent entity) {
        // Llama a la versión enriquecida con un nombre por defecto.
        return toResourceFromEntity(entity, "Información no disponible");
    }

    /**
     * Método de mapeo enriquecido.
     * Usado cuando ya hemos consultado el nombre del actor.
     */
    public static TraceabilityEventResource toResourceFromEntity(TraceabilityEvent entity, String actorName) {
        var locationResource = new TraceabilityEventResource.LocationResource(
                entity.getLocation().getLatitude(),
                entity.getLocation().getLongitude(),
                entity.getLocation().getAddress(),
                entity.getLocation().getCity(),
                entity.getLocation().getRegion(),
                entity.getLocation().getCountry()
        );

        return new TraceabilityEventResource(
                entity.getId(),
                entity.getBatchId(),
                entity.getEventType(),
                entity.getEventDate(),
                entity.getActorId(),
                // Si el actorName es nulo, usamos un valor por defecto.
                actorName != null ? actorName : "Usuario Desconocido",
                locationResource,
                entity.getBlockchainStatus().name(),
                entity.getTransactionHash(),
                entity.getProofImageUrl(),
                entity.getProofImageHash()
        );
    }
}