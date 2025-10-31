// domain/repository/TraceabilityRepository.java
package com.foodchain.blockchain_worker_context.domain.repository;

import com.foodchain.blockchain_worker_context.domain.model.entities.TraceabilityEvent;
import java.util.Optional;
import java.util.UUID;

public interface TraceabilityRepository {
    /**
     * Guarda el estado actualizado de la entidad TraceabilityEvent.
     * Típicamente será una operación de UPDATE.
     */
    void save(TraceabilityEvent event);

    /**
     * Busca un evento por su identificador único para poder actualizarlo.
     */
    Optional<TraceabilityEvent> findById(UUID id);
}