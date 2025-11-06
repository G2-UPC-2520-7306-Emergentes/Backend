// EN: traceability-context/src/main/java/com/foodchain/traceability_context/application/internal/queryservices/TraceabilityQueryServiceImpl.java
package com.foodchain.traceability_context.application.internal.queryservices;

import com.foodchain.traceability_context.application.outbound.batches.BatchQueryService;
import com.foodchain.traceability_context.domain.model.entities.TraceabilityEvent;
import com.foodchain.traceability_context.domain.model.queries.GetPublicTraceabilityEventsByBatchIdQuery;
import com.foodchain.traceability_context.domain.model.queries.GetTraceabilityEventsByBatchIdQuery;
import com.foodchain.traceability_context.domain.repository.TraceabilityRepository;
import com.foodchain.traceability_context.domain.services.TraceabilityQueryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TraceabilityQueryServiceImpl implements TraceabilityQueryService {

    private final TraceabilityRepository traceabilityRepository;
    private final BatchQueryService batchQueryService;

    public TraceabilityQueryServiceImpl(TraceabilityRepository traceabilityRepository, BatchQueryService batchQueryService) {
        this.traceabilityRepository = traceabilityRepository;
        this.batchQueryService = batchQueryService;
    }

    /**
     * Maneja la consulta para obtener el historial de un lote para un actor autenticado (privado).
     * Siempre opera de forma paginada.
     */
    @Override
    public Page<TraceabilityEvent> handle(GetTraceabilityEventsByBatchIdQuery query, Pageable pageable) {
        // 1. VERIFICAR PROPIEDAD
        if (!batchQueryService.verifyBatchOwnership(query.batchId(), query.enterpriseId())) {
            throw new SecurityException("No tiene permisos para ver el historial de este lote.");
        }

        // 2. SI LA PROPIEDAD ES VÁLIDA, DEVOLVER LOS EVENTOS PAGINADOS
        return traceabilityRepository.findByBatchId(query.batchId(), pageable);
    }

    /**
     * Maneja la consulta para obtener el historial de un lote para un consumidor (público).
     * Siempre opera de forma paginada.
     */
    @Override
    public Page<TraceabilityEvent> handle(GetPublicTraceabilityEventsByBatchIdQuery query, Pageable pageable) {
        // 1. Preguntar al batch-management-context por el estado del lote.
        String batchStatus = batchQueryService.getBatchStatus(query.batchId())
                .orElseThrow(() -> new EntityNotFoundException("El lote no existe."));

        // 2. Aplicar la regla de negocio de visibilidad pública
        if (!"FOR_SALE".equals(batchStatus) && !"CLOSED".equals(batchStatus)) {
            throw new SecurityException("La información de trazabilidad para este lote aún no es pública.");
        }

        // 3. Si la regla se cumple, devolver el historial paginado.
        return traceabilityRepository.findByBatchId(query.batchId(), pageable);
    }

    /**
     * Maneja la consulta para obtener el historial PÚBLICO COMPLETO (sin paginar) de un lote.
     * Usado específicamente para el endpoint del mapa de ruta.
     */
    @Override
    public List<TraceabilityEvent> handle(GetPublicTraceabilityEventsByBatchIdQuery query) {
        // Reutilizamos la misma lógica de negocio para verificar el estado
        String batchStatus = batchQueryService.getBatchStatus(query.batchId())
                .orElseThrow(() -> new EntityNotFoundException("El lote no existe."));

        if (!"FOR_SALE".equals(batchStatus) && !"CLOSED".equals(batchStatus)) {
            throw new SecurityException("La información de trazabilidad para este lote aún no es pública.");
        }

        // Llamamos al método no paginado del repositorio
        return traceabilityRepository.findByBatchId(query.batchId());
    }
}