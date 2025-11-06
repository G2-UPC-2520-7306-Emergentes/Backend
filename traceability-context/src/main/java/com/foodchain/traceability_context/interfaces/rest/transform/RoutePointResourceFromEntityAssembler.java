// interfaces/rest/transform/RoutePointResourceFromEntityAssembler.java
package com.foodchain.traceability_context.interfaces.rest.transform;

import com.foodchain.traceability_context.domain.model.entities.TraceabilityEvent;
import com.foodchain.traceability_context.interfaces.rest.resources.RoutePointResource;

public class RoutePointResourceFromEntityAssembler {
    public static RoutePointResource toResourceFromEntity(TraceabilityEvent entity, String actorName) {
        return new RoutePointResource(
                entity.getId(),
                entity.getEventType(),
                entity.getEventDate(),
                actorName,
                entity.getLocation() != null ? entity.getLocation().getLatitude() : null,
                entity.getLocation() != null ? entity.getLocation().getLongitude() : null,
                entity.getLocation() != null ? entity.getLocation().getAddress() : null
        );
    }
}