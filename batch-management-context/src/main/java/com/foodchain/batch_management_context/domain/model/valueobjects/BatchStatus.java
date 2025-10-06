// domain/model/valueobjects/BatchStatus.java
package com.foodchain.batch_management_context.domain.model.valueobjects;

public enum BatchStatus {
    OPEN,   // El lote está activo y puede tener eventos de trazabilidad asociados.
    CLOSED  // El lote ha finalizado su ciclo de vida y no puede ser modificado.
}