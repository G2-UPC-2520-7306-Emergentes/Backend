// domain/model/commands/DeleteBatchCommand.java
package com.foodchain.batch_management_context.domain.model.commands;

import java.util.UUID;

/**
 * Comando para eliminar un lote.
 * @param batchId El ID del lote a eliminar.
 * @param enterpriseId El ID de la empresa del usuario que solicita la eliminación.
 */
public record DeleteBatchCommand(
        UUID batchId,
        UUID enterpriseId
) {}