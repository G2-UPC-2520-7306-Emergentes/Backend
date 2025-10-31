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

    @Column
    private String imageUrl;

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
     * MÉTODO DE NEGOCIO: Actualiza los detalles de un lote.
     * Encapsula la regla de que solo un lote abierto puede ser modificado.
     * @param newProductDescription La nueva descripción del producto.
     */
    public void updateDetails(String newProductDescription) {
        // Guard Clause: Proteger la invariante de que un lote cerrado es inmutable.
        if (this.status == BatchStatus.CLOSED) {
            throw new IllegalStateException("No se puede editar un lote que ya está cerrado.");
        }
        if (newProductDescription == null || newProductDescription.isBlank()) {
            throw new IllegalArgumentException("La descripción del producto no puede estar vacía.");
        }
        this.productDescription = newProductDescription;
    }

    /**
     * FACTORY METHOD: Punto de entrada para crear un nuevo lote como duplicado de otro.
     * @param originalBatch El lote original que se usará como plantilla.
     * @return una nueva instancia de Batch con datos copiados y una nueva identidad.
     */
    public static Batch duplicateFrom(Batch originalBatch) {
        // La lógica de negocio de la duplicación vive aquí.
        // Copiamos la descripción y el ID de la empresa.
        // La nueva instancia obtendrá un ID, fecha de creación y estado 'OPEN' nuevos
        // por defecto en su constructor.
        return new Batch(originalBatch.getEnterpriseId(), originalBatch.getProductDescription());
    }

    /**
     * MÉTODO DE NEGOCIO: Asigna una URL de imagen a un lote.
     * La lógica de negocio podría incluir validaciones de formato de URL, etc.
     * @param imageUrl La URL pública de la imagen.
     */
    public void assignImageUrl(String imageUrl) {
        // Guard Clause: Proteger la invariante de que un lote cerrado es inmutable.
        if (this.status == BatchStatus.CLOSED) {
            throw new IllegalStateException("No se puede cambiar la imagen de un lote que ya está cerrado.");
        }
        this.imageUrl = imageUrl;
    }
}