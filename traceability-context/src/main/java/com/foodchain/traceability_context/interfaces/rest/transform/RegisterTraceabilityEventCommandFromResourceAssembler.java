// interfaces/rest/transform/RegisterTraceabilityEventCommandFromResourceAssembler.java
package com.foodchain.traceability_context.interfaces.rest.transform;

import com.foodchain.traceability_context.domain.model.commands.RegisterTraceabilityEventCommand;
import com.foodchain.traceability_context.interfaces.rest.resources.RegisterStepResource;
import java.util.UUID;

public class RegisterTraceabilityEventCommandFromResourceAssembler {
    /**
     * Traduce el DTO de recurso a un Comando de dominio.
     * @param resource El DTO de la petición.
     * @param actorId El ID del usuario autenticado, extraído del token JWT.
     * @return El comando listo para ser procesado por la capa de aplicación.
     */
    public static RegisterTraceabilityEventCommand toCommandFromResource(RegisterStepResource resource, UUID actorId) {
        return new RegisterTraceabilityEventCommand(
                resource.batchId(),
                resource.eventType(),
                actorId,
                resource.latitude(),
                resource.longitude()
        );
    }
}