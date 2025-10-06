// infrastructure/persistence/jpa/repositories/BatchRepositoryImpl.java
package com.foodchain.batch_management_context.infrastructure.persistence.jpa.repositories;

import com.foodchain.batch_management_context.domain.model.aggregates.Batch;
import com.foodchain.batch_management_context.domain.model.valueobjects.BatchId;
import com.foodchain.batch_management_context.domain.repositories.BatchRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class BatchRepositoryImpl implements BatchRepository {

    private final BatchJpaRepository batchJpaRepository;

    public BatchRepositoryImpl(BatchJpaRepository batchJpaRepository) {
        this.batchJpaRepository = batchJpaRepository;
    }

    @Override
    public void save(Batch batch) {
        batchJpaRepository.save(batch);
    }

    @Override
    public Optional<Batch> findById(BatchId batchId) {
        return batchJpaRepository.findById(batchId);
    }
}