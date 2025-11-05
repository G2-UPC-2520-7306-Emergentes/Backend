// interfaces/rest/transform/TraceabilityEventResourceFromEntityAssembler.java
package com.foodchain.traceability_context.interfaces.rest.transform;

import com.foodchain.traceability_context.domain.model.entities.TraceabilityEvent;
import com.foodchain.traceability_context.interfaces.rest.resources.TraceabilityEventResource;

public class TraceabilityEventResourceFromEntityAssembler {

    public static TraceabilityEventResource toResourceFromEntity(TraceabilityEvent entity) {
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
                locationResource,
                entity.getBlockchainStatus().name(),
                entity.getTransactionHash(),
                entity.getProofImageUrl(),
                entity.getProofImageHash()
        );
    }
}