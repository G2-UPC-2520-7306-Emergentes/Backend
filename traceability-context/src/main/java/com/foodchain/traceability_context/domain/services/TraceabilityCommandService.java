// domain/services/TraceabilityCommandService.java
package com.foodchain.traceability_context.domain.services;

import com.foodchain.traceability_context.domain.model.commands.RegisterTraceabilityEventCommand;

import java.util.UUID;

public interface TraceabilityCommandService {
    UUID handle(RegisterTraceabilityEventCommand command);
}