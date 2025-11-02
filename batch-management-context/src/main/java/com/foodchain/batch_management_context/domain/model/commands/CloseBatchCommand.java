// domain/model/commands/CloseBatchCommand.java
package com.foodchain.batch_management_context.domain.model.commands;

import java.util.UUID;

/**
 * Comando para cerrar un lote.
 * @param batchId El ID del lote a cerrar.
 * @param enterpriseId El ID de la empresa del usuario que solicita el cierre.
 */
public record CloseBatchCommand(
        UUID batchId,
        UUID enterpriseId
) {}