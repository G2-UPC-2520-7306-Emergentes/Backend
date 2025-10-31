// domain/model/commands/DuplicateBatchCommand.java
package com.foodchain.batch_management_context.domain.model.commands;

import java.util.UUID;

/**
 * Comando para duplicar un lote existente.
 * @param originalBatchId El ID del lote que servirá como plantilla.
 * @param enterpriseId El ID de la empresa del usuario que solicita la duplicación.
 */
public record DuplicateBatchCommand(
        UUID originalBatchId,
        UUID enterpriseId
) {}