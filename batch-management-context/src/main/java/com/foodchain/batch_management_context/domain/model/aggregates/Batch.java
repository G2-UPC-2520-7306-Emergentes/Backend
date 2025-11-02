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

    public void close() {
        if (this.status == BatchStatus.CLOSED) {
            throw new IllegalStateException("No se puede cerrar un lote que ya está cerrado.");
        }
        this.status = BatchStatus.CLOSED;
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

    public void startProcessing() {
        if (this.status != BatchStatus.OPEN) {
            throw new IllegalStateException("Solo se puede iniciar el procesamiento de un lote en estado 'OPEN'.");
        }
        this.status = BatchStatus.IN_PROCESSING;
    }

    public void finishProcessing() {
        if (this.status != BatchStatus.IN_PROCESSING) {
            throw new IllegalStateException("Solo se puede finalizar el procesamiento de un lote en estado 'IN_PROCESSING'.");
        }
        this.status = BatchStatus.PROCESSED;
    }

    public void startPacking() {
        if (this.status != BatchStatus.PROCESSED) {
            throw new IllegalStateException("Solo se puede iniciar el empaque de un lote en estado 'PROCESSED'.");
        }
        this.status = BatchStatus.PACKING;
    }

    public void finishPacking() {
        if (this.status != BatchStatus.PACKING) {
            throw new IllegalStateException("Solo se puede finalizar el empaque de un lote en estado 'PACKING'.");
        }
        this.status = BatchStatus.PACKED;
    }

    public void shipToDistributor() {
        if (this.status != BatchStatus.PACKED) {
            throw new IllegalStateException("Solo se puede enviar a distribución un lote en estado 'PACKED'.");
        }
        this.status = BatchStatus.IN_TRANSIT;
    }

    public void receiveInWarehouse() {
        if (this.status != BatchStatus.IN_TRANSIT) {
            throw new IllegalStateException("Solo se puede recibir en almacén un lote en estado 'IN_TRANSIT'.");
        }
        this.status = BatchStatus.IN_WAREHOUSE;
    }

    public void markForSale() {
        // Un lote puede ponerse a la venta desde el almacén o si llega directo a tienda.
        if (this.status != BatchStatus.IN_WAREHOUSE && this.status != BatchStatus.IN_TRANSIT) {
            throw new IllegalStateException("El lote no está en una etapa válida para marcarse para la venta.");
        }
        this.status = BatchStatus.FOR_SALE;
    }
}