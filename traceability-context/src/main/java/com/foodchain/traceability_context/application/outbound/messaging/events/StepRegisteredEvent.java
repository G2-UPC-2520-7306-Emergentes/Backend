// application/outbound/messaging/events/StepRegisteredEvent.java
package com.foodchain.traceability_context.application.outbound.messaging.events;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * DTO (Data Transfer Object) que representa el evento de dominio "StepRegistered".
 * Este es el contrato de mensaje que se recibe desde la cola de RabbitMQ.
 * Debe ser una réplica exacta del evento que publica el 'traceability-context'.
 * Implementa Serializable para ser compatible con diferentes estrategias de mensajería.
 *
 * @param eventId El ID único del evento de trazabilidad que fue registrado.
 * @param batchId El ID del lote al que pertenece este evento.
 * @param eventType El tipo de evento (ej. "COSECHA_MANUAL").
 * @param eventDate La fecha y hora en que se registró el evento.
 * @param actorId El ID del usuario que realizó el registro.
 * @param location Un objeto que contiene la latitud y longitud del evento.
 */
public record StepRegisteredEvent(
        UUID eventId,
        UUID batchId,
        String eventType,
        Date eventDate,
        UUID actorId,
        LocationDTO location
) implements Serializable { // Es una buena práctica que los DTOs de eventos sean serializables

    /**
     * DTO anidado para representar la ubicación.
     * Esto mantiene el contrato limpio y estructurado.
     */
    public record LocationDTO(
            Double latitude,
            Double longitude
    ) implements Serializable {}
}