package com.foodchain.blockchain_worker_context.infrastructure.persistence.jpa.repositories;

import com.foodchain.blockchain_worker_context.domain.model.entities.TraceabilityEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

// infrastructure/persistence/jpa/repositories/TraceabilityJpaRepository.java
@Repository
public interface TraceabilityJpaRepository extends JpaRepository<TraceabilityEvent, UUID> {
    // No necesitamos métodos custom aquí, JpaRepository nos da findById y save.
}