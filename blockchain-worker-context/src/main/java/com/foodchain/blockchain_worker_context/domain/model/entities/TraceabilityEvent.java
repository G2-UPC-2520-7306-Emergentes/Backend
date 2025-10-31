// domain/model/entities/TraceabilityEvent.java
package com.foodchain.blockchain_worker_context.domain.model.entities;

import com.foodchain.blockchain_worker_context.domain.model.valueobjects.BlockchainStatus;
import com.foodchain.blockchain_worker_context.domain.model.valueobjects.Location;
import com.foodchain.shared_domain.domain.model.aggregates.AuditableAbstractAggregateRoot; // Usamos la base para la auditoría
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

    /**
     * Constructor privado. La creación se fuerza a través del factory method.
     */
    private TraceabilityEvent(UUID batchId, String eventType, UUID actorId, Location location) {
        this.id = UUID.randomUUID();
        this.batchId = batchId;
        this.eventType = eventType;
        this.actorId = actorId;
        this.location = location;
        this.eventDate = new Date();
        this.blockchainStatus = BlockchainStatus.PENDING; // eL evento nace como 'PENDIENTE'.
    }

    /**
     * FACTORY METHOD: Punto de entrada controlado para registrar un nuevo evento.
     */
    public static TraceabilityEvent record(UUID batchId, String eventType, UUID actorId, Location location) {
        // Guard Clauses para proteger la creación
        if (batchId == null || eventType == null || eventType.isBlank() || actorId == null || location == null) {
            throw new IllegalArgumentException("All fields are required to record a traceability event.");
        }
        return new TraceabilityEvent(batchId, eventType, actorId, location);
    }

    /**
     * METODO DE NEGOCIO: Confirma que el evento ha sido anclado en la blockchain.
     */
    public void confirmAnchoring(String transactionHash) {
        if (this.blockchainStatus != BlockchainStatus.PENDING) {
            throw new IllegalStateException("Cannot confirm anchoring on an event that is not in PENDING state.");
        }
        if (transactionHash == null || transactionHash.isBlank()) {
            throw new IllegalArgumentException("Transaction hash cannot be null or empty.");
        }
        this.transactionHash = transactionHash;
        this.blockchainStatus = BlockchainStatus.CONFIRMED;
    }

    /**
     * METODO DE NEGOCIO: Marca el anclaje como fallido.
     */
    public void markAsFailed() {
        if (this.blockchainStatus != BlockchainStatus.PENDING) {
            throw new IllegalStateException("Cannot mark as failed an event that is not in PENDING state.");
        }
        this.blockchainStatus = BlockchainStatus.FAILED;
    }
}