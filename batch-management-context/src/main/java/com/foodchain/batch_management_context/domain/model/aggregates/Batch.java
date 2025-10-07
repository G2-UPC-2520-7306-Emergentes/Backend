// domain/model/aggregates/Batch.java
package com.foodchain.batch_management_context.domain.model.aggregates;

import com.foodchain.batch_management_context.domain.model.valueobjects.BatchId;
import com.foodchain.batch_management_context.domain.model.valueobjects.BatchStatus;
import com.foodchain.shared_domain.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.UUID;

/**
 * AGGREGATE ROOT: Batch
 * Representa un lote de productos. Es la raíz de consistencia para todas las
 * operaciones relacionadas con el ciclo de vida del lote.
 */
@Entity
@Table(name = "batches")
@Getter
@NoArgsConstructor
public class Batch extends AuditableAbstractAggregateRoot<Batch> {

    @EmbeddedId
    private BatchId batchId;

    @Column(nullable = false)
    private UUID enterpriseId;

    @Column(nullable = false)
    private String productDescription;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date creationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BatchStatus status;

    /**
     * Constructor privado. La creación se gestiona a través del factory method.
     */
    private Batch(UUID enterpriseId, String productDescription) {
        this.batchId = new BatchId();
        this.enterpriseId = enterpriseId;
        this.productDescription = productDescription;
        this.creationDate = new Date();
        this.status = BatchStatus.OPEN; // Un nuevo lote siempre nace 'ABIERTO'.
    }

    /**
     * FACTORY METHOD: Punto de entrada para crear un nuevo lote.
     * @return una instancia válida de Batch.
     */
    public static Batch create(UUID enterpriseId, String productDescription) {
        // Guard Clauses para proteger las invariantes
        if (enterpriseId == null || productDescription == null || productDescription.isBlank()) {
            throw new IllegalArgumentException("Enterprise ID and product description are required to create a batch.");
        }
        return new Batch(enterpriseId, productDescription);
    }

    /**
     * MÉTODO DE NEGOCIO: Cierra el lote.
     * Encapsula la regla de que solo un lote abierto puede ser cerrado.
     */
    public void close() {
        if (this.status == BatchStatus.CLOSED) {
            throw new IllegalStateException("Cannot close a batch that is already closed.");
        }
        this.status = BatchStatus.CLOSED;
    }
}