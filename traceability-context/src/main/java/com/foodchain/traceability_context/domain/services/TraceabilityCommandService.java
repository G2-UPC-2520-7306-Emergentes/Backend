// domain/services/TraceabilityCommandService.java
package com.foodchain.traceability_context.domain.services;

import com.foodchain.traceability_context.domain.model.commands.CorrectTraceabilityEventCommand;
import com.foodchain.traceability_context.domain.model.commands.RegisterTraceabilityEventCommand;
import com.foodchain.traceability_context.domain.model.entities.TraceabilityEvent;

public interface TraceabilityCommandService {
    TraceabilityEvent handle(RegisterTraceabilityEventCommand command); // ¡Devuelve la entidad!
    TraceabilityEvent handle(CorrectTraceabilityEventCommand command); // ¡Devuelve la entidad!
}