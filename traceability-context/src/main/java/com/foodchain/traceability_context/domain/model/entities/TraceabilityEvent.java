// domain/model/entities/TraceabilityEvent.java
package com.foodchain.traceability_context.domain.model.entities;

import com.foodchain.shared_domain.domain.model.aggregates.AuditableAbstractAggregateRoot; // Usamos la base para la auditoría
import com.foodchain.traceability_context.domain.model.valueobjects.BlockchainStatus;
import com.foodchain.traceability_context.domain.model.valueobjects.Location;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

/**
 * ENTITY: TraceabilityEvent
 * Representa un registro inmutable en la historia de un lote.
 */
@Entity
@Table(name = "traceability_events")
@Getter
@NoArgsConstructor
public class TraceabilityEvent extends AuditableAbstractAggregateRoot<TraceabilityEvent> {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID batchId;

    @Column(nullable = false)
    private String eventType;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date eventDate;

    @Column(nullable = false)
    private UUID actorId;

    @Embedded
    private Location location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BlockchainStatus blockchainStatus;

    @Column(unique = true) // El hash de la transacción debe ser único
    private String transactionHash;

    @Column
    private String proofImageUrl; // URL donde se almacena la imagen

    @Column
    private String proofImageHash; // Hash SHA-256 del contenido de la imagen

    @Column
    private UUID correctedEventId;

    @Column(columnDefinition = "TEXT")
    private String justification;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date clientCreatedAt; // El timestamp de cuando el cliente creó el borrador. Puede ser nulo.

    /**
     * Constructor privado. La creación se fuerza a través del factory method.
     */
    private TraceabilityEvent(UUID batchId, String eventType, UUID actorId, Location location, String proofImageUrl, String proofImageHash) {
        this.id = UUID.randomUUID();
        this.batchId = batchId;
        this.eventType = eventType;
        this.actorId = actorId;
        this.location = location;
        this.eventDate = new Date();
        this.blockchainStatus = BlockchainStatus.PENDING;
        this.proofImageUrl = proofImageUrl;
        this.proofImageHash = proofImageHash;
    }

    /**
     * FACTORY METHOD actualizado para incluir el timestamp del cliente.
     */
    public static TraceabilityEvent record(UUID batchId, String eventType, UUID actorId, Location location,
                                           String proofImageUrl, String proofImageHash, Date clientCreatedAt) {
        if (batchId == null || eventType == null || eventType.isBlank() || actorId == null || location == null) {
            throw new IllegalArgumentException("Todos los campos básicos son requeridos para registrar un evento.");
        }
        var event = new TraceabilityEvent(batchId, eventType, actorId, location, proofImageUrl, proofImageHash);
        event.clientCreatedAt = clientCreatedAt; // Asignamos el timestamp del cliente
        return event;
    }

    /**
     * FACTORY METHOD: Punto de entrada para crear un EVENTO DE CORRECCIÓN.
     */
    public static TraceabilityEvent recordCorrection(UUID originalEventId, UUID batchId, UUID actorId, Location location, String justification, String proofImageUrl, String proofImageHash) {
        if (originalEventId == null || justification == null || justification.isBlank()) {
            throw new IllegalArgumentException("Se requiere el ID del evento original y una justificación para la corrección.");
        }

        // Creamos un evento normal, pero con los campos de corrección llenos.
        var correctionEvent = new TraceabilityEvent(batchId, "CORRECTION", actorId, location, proofImageUrl, proofImageHash);
        correctionEvent.correctedEventId = originalEventId;
        correctionEvent.justification = justification;

        return correctionEvent;
    }
}