// infrastructure/persistence/jpa/repositories/TraceabilityJpaRepository.java
package com.foodchain.traceability_context.infrastructure.persistence.jpa.repositories;

import com.foodchain.traceability_context.domain.model.entities.TraceabilityEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TraceabilityJpaRepository extends JpaRepository<TraceabilityEvent, UUID> {
    List<TraceabilityEvent> findByBatchId(UUID batchId);
    /**
     * Busca el evento más reciente para un batchId específico, ordenando por fecha de evento en orden descendente.
     * "findTop" limita el resultado a 1.
     */
    Optional<TraceabilityEvent> findTopByBatchIdOrderByEventDateDesc(UUID batchId);
    Page<TraceabilityEvent> findByBatchId(UUID batchId, Pageable pageable);
}