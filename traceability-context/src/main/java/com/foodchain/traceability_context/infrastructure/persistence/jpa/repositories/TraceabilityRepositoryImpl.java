// infrastructure/persistence/jpa/repositories/TraceabilityRepositoryImpl.java
package com.foodchain.traceability_context.infrastructure.persistence.jpa.repositories;

import com.foodchain.traceability_context.domain.model.entities.TraceabilityEvent;
import com.foodchain.traceability_context.domain.repository.TraceabilityRepository;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class TraceabilityRepositoryImpl implements TraceabilityRepository {
    private final TraceabilityJpaRepository jpaRepository;

    public TraceabilityRepositoryImpl(TraceabilityJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(TraceabilityEvent event) {
        jpaRepository.save(event);
    }

    @Override
    public Optional<TraceabilityEvent> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<TraceabilityEvent> findByBatchId(UUID batchId) {
        return jpaRepository.findByBatchId(batchId);
    }
}