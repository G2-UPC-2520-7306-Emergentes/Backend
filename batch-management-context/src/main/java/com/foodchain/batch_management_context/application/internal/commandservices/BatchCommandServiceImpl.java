// application/internal/commandservices/BatchCommandServiceImpl.java
package com.foodchain.batch_management_context.application.internal.commandservices;

import com.foodchain.batch_management_context.application.outbound.storage.FileStorageService;
import com.foodchain.batch_management_context.application.outbound.traceability.TraceabilityService;
import com.foodchain.batch_management_context.domain.model.aggregates.Batch;
import com.foodchain.batch_management_context.domain.model.commands.*;
import com.foodchain.batch_management_context.domain.model.valueobjects.BatchId;
import com.foodchain.batch_management_context.domain.repositories.BatchRepository;
import com.foodchain.batch_management_context.domain.services.BatchCommandService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class BatchCommandServiceImpl implements BatchCommandService {

    private final BatchRepository batchRepository;
    private final TraceabilityService traceabilityService;

    private final FileStorageService fileStorageService; // ¡INYECTAMOS EL SERVICIO DE FICHEROS!

    public BatchCommandServiceImpl(BatchRepository batchRepository, TraceabilityService traceabilityService, FileStorageService fileStorageService) {
        this.batchRepository = batchRepository;
        this.traceabilityService = traceabilityService;
        this.fileStorageService = fileStorageService;
    }
    @Override
    public BatchId handle(CreateBatchCommand command) {
        // Utiliza el factory method del Agregado para crear una nueva instancia
        var batch = Batch.create(command.enterpriseId(), command.productDescription());

        // Persiste el nuevo agregado a través del repositorio
        batchRepository.save(batch);

        // Devuelve el identificador único del nuevo lote
        return batch.getBatchId();
    }

    @Override
    @Transactional
    public void handle(EditBatchCommand command) {
        // 1. Buscar el agregado por su ID
        var batch = batchRepository.findById(new BatchId(command.batchId()))
                .orElseThrow(() -> new EntityNotFoundException("Lote no encontrado con ID: " + command.batchId()));

        // 2. Validar propiedad: El lote pertenece a la empresa del usuario?
        if (!batch.getEnterpriseId().equals(command.enterpriseId())) {
            throw new SecurityException("No tiene permisos para editar un lote que no pertenece a su empresa.");
        }

        // 3. Delegar la lógica de negocio al método del agregado
        batch.updateDetails(command.productDescription());

        // 4. Persistir los cambios (gracias a @Transactional, esto podría ser automático)
        batchRepository.save(batch);
    }

    @Override
    @Transactional
    public BatchId handle(DuplicateBatchCommand command) {
        // 1. Buscar el lote original
        var originalBatch = batchRepository.findById(new BatchId(command.originalBatchId()))
                .orElseThrow(() -> new EntityNotFoundException("Lote original no encontrado con ID: " + command.originalBatchId()));

        // 2. Validar propiedad
        if (!originalBatch.getEnterpriseId().equals(command.enterpriseId())) {
            throw new SecurityException("No tiene permisos para duplicar un lote que no pertenece a su empresa.");
        }

        // 3. Usar el factory method del dominio para crear la nueva instancia
        var newBatch = Batch.duplicateFrom(originalBatch);

        // 4. Persistir el NUEVO lote
        batchRepository.save(newBatch);

        // 5. Devolver el ID del NUEVO lote
        return newBatch.getBatchId();
    }

    @Override
    @Transactional
    public void handle(DeleteBatchCommand command) {
        // 1. Buscar el lote y validar propiedad
        var batch = batchRepository.findById(new BatchId(command.batchId()))
                .orElseThrow(() -> new EntityNotFoundException("Lote no encontrado: " + command.batchId()));

        if (!batch.getEnterpriseId().equals(command.enterpriseId())) {
            throw new SecurityException("No tiene permisos para eliminar este lote.");
        }

        // 2. ¡LA REGLA DE NEGOCIO CRÍTICA! Preguntar al otro servicio.
        if (traceabilityService.hasTraceabilityEvents(command.batchId())) {
            throw new IllegalStateException("No se puede eliminar un lote que ya tiene un historial de trazabilidad.");
        }

        // 3. Si se cumplen todas las condiciones, eliminar.
        batchRepository.delete(batch);
    }

    @Override
    @Transactional
    public void handle(AssignImageToBatchCommand command) {
        // 1. Delegar el almacenamiento del fichero al servicio de infraestructura
        String imageUrl = fileStorageService.store(command.file());

        // 2. Buscar el agregado y validar propiedad
        var batch = batchRepository.findById(new BatchId(command.batchId()))
                .orElseThrow(() -> new EntityNotFoundException("Lote no encontrado: " + command.batchId()));

        if (!batch.getEnterpriseId().equals(command.enterpriseId())) {
            throw new SecurityException("No tiene permisos para modificar este lote.");
        }

        // 3. Llamar al método de negocio del agregado
        batch.assignImageUrl(imageUrl);

        // 4. Persistir los cambios
        batchRepository.save(batch);
    }
}