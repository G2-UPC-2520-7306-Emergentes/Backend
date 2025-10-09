// infrastructure/persistence/jpa/repositories/TraceabilityJpaRepository.java
package com.foodchain.traceability_context.infrastructure.persistence.jpa.repositories;

import com.foodchain.traceability_context.domain.model.entities.TraceabilityEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface TraceabilityJpaRepository extends JpaRepository<TraceabilityEvent, UUID> {
    List<TraceabilityEvent> findByBatchId(UUID batchId);
}