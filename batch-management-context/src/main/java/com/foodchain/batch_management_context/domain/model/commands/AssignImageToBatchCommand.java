// domain/model/commands/AssignImageToBatchCommand.java
package com.foodchain.batch_management_context.domain.model.commands;

import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

/**
 * Comando para asignar una imagen a un lote.
 * @param batchId El ID del lote a modificar.
 * @param enterpriseId El ID de la empresa del usuario que sube la imagen.
 * @param file El archivo de imagen.
 */
public record AssignImageToBatchCommand(
        UUID batchId,
        UUID enterpriseId,
        MultipartFile file
) {}