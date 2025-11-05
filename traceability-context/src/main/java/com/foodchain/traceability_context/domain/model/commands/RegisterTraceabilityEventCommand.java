// domain/model/commands/RegisterTraceabilityEventCommand.java
package com.foodchain.traceability_context.domain.model.commands;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.Date; // ¡Añadimos Date!

/**
 * Comando para registrar un nuevo evento de trazabilidad para un lote existente.
 * Es un DTO inmutable que encapsula todos los datos necesarios para el caso de uso.
 * @param batchId El ID del lote al que pertenece este evento.
 * @param eventType Una descripción del evento (ej. "COSECHA", "TRANSPORTE").
 * @param actorId El ID del usuario que registra el evento.
 * @param latitude La latitud donde ocurrió el evento.
 * @param longitude La longitud donde ocurrió el evento.
 */
public record RegisterTraceabilityEventCommand(
        UUID batchId,
        String eventType,
        UUID actorId,
        Double latitude,
        Double longitude,
        MultipartFile proofImageFile,
        Date clientCreatedAt // ¡NUEVO CAMPO! Puede ser nulo.
) {}