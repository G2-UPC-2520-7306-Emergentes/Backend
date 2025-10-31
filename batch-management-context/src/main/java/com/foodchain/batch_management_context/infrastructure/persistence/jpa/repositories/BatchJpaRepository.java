// infrastructure/persistence/jpa/repositories/BatchJpaRepository.java
package com.foodchain.batch_management_context.infrastructure.persistence.jpa.repositories;

import com.foodchain.batch_management_context.domain.model.aggregates.Batch;
import com.foodchain.batch_management_context.domain.model.valueobjects.BatchId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BatchJpaRepository extends JpaRepository<Batch, BatchId> {
    List<Batch> findByEnterpriseId(UUID enterpriseId);
}