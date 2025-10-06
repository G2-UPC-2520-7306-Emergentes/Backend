// domain/repository/BatchRepository.java
package com.foodchain.batch_management_context.domain.repositories;

import com.foodchain.batch_management_context.domain.model.aggregates.Batch;
import com.foodchain.batch_management_context.domain.model.valueobjects.BatchId;
import java.util.Optional;

public interface BatchRepository {
    void save(Batch batch);
    Optional<Batch> findById(BatchId batchId);
}