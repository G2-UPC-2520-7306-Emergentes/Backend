// application/internal/queryservices/TraceabilityQueryServiceImpl.java
package com.foodchain.traceability_context.application.internal.queryservices;

import com.foodchain.traceability_context.application.outbound.batches.BatchQueryService;
import com.foodchain.traceability_context.application.outbound.iam.UserQueryService;
import com.foodchain.traceability_context.domain.model.entities.TraceabilityEvent;
import com.foodchain.traceability_context.domain.model.queries.GetHistoryByBatchIdQuery;
import com.foodchain.traceability_context.domain.model.queries.GetPublicTraceabilityEventsByBatchIdQuery;
import com.foodchain.traceability_context.domain.model.queries.GetTraceabilityEventsByBatchIdQuery;
import com.foodchain.traceability_context.domain.repository.TraceabilityRepository;
import com.foodchain.traceability_context.domain.services.TraceabilityQueryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TraceabilityQueryServiceImpl implements TraceabilityQueryService {
    private final TraceabilityRepository traceabilityRepository;
    private final BatchQueryService batchQueryService; // El cliente que habla con batch-management
    private final UserQueryService userQueryService;


    public TraceabilityQueryServiceImpl(TraceabilityRepository traceabilityRepository, BatchQueryService b, UserQueryService u) {
        this.traceabilityRepository = traceabilityRepository;
        this.batchQueryService = b;
        this.userQueryService = u;
    }

    @Override
    public List<TraceabilityEvent> handle(GetHistoryByBatchIdQuery query) {
        // Simplemente delegamos la búsqueda al repositorio
        return traceabilityRepository.findByBatchId(query.batchId());
    }

    @Override
    public List<TraceabilityEvent> handle(GetTraceabilityEventsByBatchIdQuery query) {
        // 1. VERIFICAR PROPIEDAD
        if (!batchQueryService.verifyBatchOwnership(query.batchId(), query.enterpriseId())) {
            throw new SecurityException("No tiene permisos para ver el historial de este lote.");
        }
        // 2. SI LA PROPIEDAD ES VÁLIDA, DEVOLVER LOS EVENTOS
        return traceabilityRepository.findByBatchId(query.batchId());
    }
    @Override
    public List<TraceabilityEvent> handle(GetPublicTraceabilityEventsByBatchIdQuery query) {
        // 1. Preguntar al batch-management-context por el estado del lote.
        String batchStatus = batchQueryService.getBatchStatus(query.batchId())
                .orElseThrow(() -> new EntityNotFoundException("El lote no existe."));

        // 2. Aplicar la regla de negocio
        if (!"FOR_SALE".equals(batchStatus) && !"CLOSED".equals(batchStatus)) {
            throw new SecurityException("La información de trazabilidad para este lote aún no es pública.");
        }

        // 3. Si la regla se cumple, devolver el historial.
        return traceabilityRepository.findByBatchId(query.batchId());
    }
    public List<TraceabilityEvent> getAndEnrichEvents(UUID batchId) {
        // 1. Obtener los eventos
        List<TraceabilityEvent> events = traceabilityRepository.findByBatchId(batchId);
        if (events.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. Extraer los IDs de los actores
        List<UUID> actorIds = events.stream().map(TraceabilityEvent::getActorId).distinct().collect(Collectors.toList());

        // 3. Llamar al identity-context para obtener los nombres
        Map<UUID, String> actorNames = userQueryService.getUsernamesForIds(actorIds);

        // 4. "Enriquecer" los objetos (aunque no modificamos la entidad, lo haremos en el Assembler)
        // Este es un buen lugar para devolver un DTO combinado si el modelo se vuelve más complejo.
        // Por ahora, pasaremos el mapa de nombres al Assembler.

        return events; // Devolvemos los eventos, el enriquecimiento final se hará en la capa de interfaz.
    }
}