// domain/model/commands/EditBatchCommand.java
package com.foodchain.batch_management_context.domain.model.commands;

import java.util.UUID;

/**
 * Comando para editar la información de un lote existente.
 * @param batchId El ID del lote a modificar.
 * @param enterpriseId El ID de la empresa del usuario que intenta la modificación (para validación de propiedad).
 * @param productDescription La nueva descripción para el producto.
 */
public record EditBatchCommand(
        UUID batchId,
        UUID enterpriseId,
        String productDescription
) {}