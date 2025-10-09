// domain/repository/TraceabilityRepository.java
package com.foodchain.traceability_context.domain.repository;

import com.foodchain.traceability_context.domain.model.entities.TraceabilityEvent;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TraceabilityRepository {
    void save(TraceabilityEvent event);
    Optional<TraceabilityEvent> findById(UUID id);
    List<TraceabilityEvent> findByBatchId(UUID batchId);
}