package com.foodchain.traceability_context.interfaces.rest.transform;

import com.foodchain.traceability_context.domain.model.commands.CorrectTraceabilityEventCommand;
import com.foodchain.traceability_context.interfaces.rest.resources.CorrectStepResource;

import java.util.UUID;

public class CorrectTraceabilityEventCommandFromResourceAssembler {
    public static CorrectTraceabilityEventCommand toCommandFromResource(UUID originalEventId, CorrectStepResource resource,java.util.UUID actorId, org.springframework.web.multipart.MultipartFile file) {
        return new CorrectTraceabilityEventCommand(
                originalEventId,
                actorId,
                resource.justification(),
                resource.latitude(),
                resource.longitude(),
                file // Pasamos el fichero al comando
        );

    }
}
