// infrastructure/persistence/jpa/repositories/BatchJpaRepository.java
package com.foodchain.batch_management_context.infrastructure.persistence.jpa.repositories;

import com.foodchain.batch_management_context.domain.model.aggregates.Batch;
import com.foodchain.batch_management_context.domain.model.valueobjects.BatchId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchJpaRepository extends JpaRepository<Batch, BatchId> {
}