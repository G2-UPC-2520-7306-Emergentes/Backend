// domain/model/valueobjects/BlockchainStatus.java
package com.foodchain.traceability_context.domain.model.valueobjects;

public enum BlockchainStatus {
    PENDING,   // El evento ha sido registrado y está en cola para ser anclado.
    CONFIRMED, // El evento ha sido anclado con éxito en la blockchain.
    FAILED     // El anclaje falló tras varios intentos.
}