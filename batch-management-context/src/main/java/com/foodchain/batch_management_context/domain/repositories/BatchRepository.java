// domain/repository/BatchRepository.java
package com.foodchain.batch_management_context.domain.repositories;

import com.foodchain.batch_management_context.domain.model.aggregates.Batch;
import com.foodchain.batch_management_context.domain.model.valueobjects.BatchId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BatchRepository {
    void save(Batch batch);
    Optional<Batch> findById(BatchId batchId);
    List<Batch> findByEnterpriseId(UUID enterpriseId);
    void delete(Batch batch);
    long countByEnterpriseId(UUID enterpriseId);
}