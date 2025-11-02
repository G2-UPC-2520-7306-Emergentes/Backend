// domain/model/valueobjects/BatchStatus.java
package com.foodchain.batch_management_context.domain.model.valueobjects;

// EN: batch-management-context/domain/model/valueobjects/BatchStatus.java
public enum BatchStatus {
    OPEN,           // Recién creado, listo para la cosecha.
    IN_PROCESSING,  // Ha llegado a la planta de procesamiento.
    PROCESSED,      // El procesamiento ha terminado.
    PACKING,        // El empaque ha comenzado.
    PACKED,         // El empaque ha terminado.
    IN_TRANSIT,     // En camino al distribuidor/retailer.
    IN_WAREHOUSE,   // En el centro de distribución.
    FOR_SALE,       // Disponible en la tienda.
    CLOSED          // El ciclo de vida ha terminado.
}