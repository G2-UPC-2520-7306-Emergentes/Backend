// infrastructure/persistence/jpa/repositories/TraceabilityRepositoryImpl.java
package com.foodchain.traceability_context.infrastructure.persistence.jpa.repositories;

import com.foodchain.traceability_context.domain.model.entities.TraceabilityEvent;
import com.foodchain.traceability_context.domain.repository.TraceabilityRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import org.springframework.data.domain.Pageable;
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
    @Override
    public Optional<TraceabilityEvent> findLatestByBatchId(UUID batchId) {
        return jpaRepository.findTopByBatchIdOrderByEventDateDesc(batchId);
    }
    @Override
    public Page<TraceabilityEvent> findByBatchId(UUID batchId, Pageable pageable) {
        return jpaRepository.findByBatchId(batchId, pageable);
    }
}