// application/outbound/messaging/events/StepRegisteredEvent.java
package com.foodchain.traceability_context.application.outbound.messaging.events;

import java.util.Date;
import java.util.UUID;

public record StepRegisteredEvent(
        UUID eventId,
        UUID batchId,
        String eventType,
        Date eventDate,
        String location // Simplificamos, se puede enviar el objeto Location completo también
) {}