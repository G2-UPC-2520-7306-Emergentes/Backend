// domain/model/commands/CorrectTraceabilityEventCommand.java
package com.foodchain.traceability_context.domain.model.commands;

import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

public record CorrectTraceabilityEventCommand(
        UUID originalEventId,
        UUID actorId,
        String justification,
        Double latitude,
        Double longitude,
        MultipartFile proofImageFile // Opcional, para adjuntar nueva evidencia
) {}