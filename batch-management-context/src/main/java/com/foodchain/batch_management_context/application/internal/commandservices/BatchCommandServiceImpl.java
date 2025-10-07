// application/internal/commandservices/BatchCommandServiceImpl.java
package com.foodchain.batch_management_context.application.internal.commandservices;

import com.foodchain.batch_management_context.domain.model.aggregates.Batch;
import com.foodchain.batch_management_context.domain.model.commands.CreateBatchCommand;
import com.foodchain.batch_management_context.domain.model.valueobjects.BatchId;
import com.foodchain.batch_management_context.domain.repositories.BatchRepository;
import com.foodchain.batch_management_context.domain.services.BatchCommandService;
import org.springframework.stereotype.Service;

@Service
public class BatchCommandServiceImpl implements BatchCommandService {

    private final BatchRepository batchRepository;

    public BatchCommandServiceImpl(BatchRepository batchRepository) {
        this.batchRepository = batchRepository;
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
}