// domain/model/commands/CreateBatchCommand.java
package com.foodchain.batch_management_context.domain.model.commands;

import java.util.UUID;

/**
 * Comando para crear un nuevo Lote.
 * Representa la intención de negocio de iniciar un nuevo lote de producción.
 * @param enterpriseId El ID de la empresa propietaria del lote.
 * @param productDescription Una descripción del producto en el lote.
 */
public record CreateBatchCommand(UUID enterpriseId, String productDescription) {
}