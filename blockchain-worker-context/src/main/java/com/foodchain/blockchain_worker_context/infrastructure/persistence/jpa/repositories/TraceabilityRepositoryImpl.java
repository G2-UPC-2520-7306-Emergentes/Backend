package com.foodchain.blockchain_worker_context.infrastructure.persistence.jpa.repositories;

import com.foodchain.blockchain_worker_context.domain.model.entities.TraceabilityEvent;
import com.foodchain.blockchain_worker_context.domain.repository.TraceabilityRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

// infrastructure/persistence/jpa/repositories/TraceabilityRepositoryImpl.java
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
}