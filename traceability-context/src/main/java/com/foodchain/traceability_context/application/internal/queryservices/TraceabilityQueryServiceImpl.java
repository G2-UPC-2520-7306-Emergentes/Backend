// application/internal/queryservices/TraceabilityQueryServiceImpl.java
package com.foodchain.traceability_context.application.internal.queryservices;

import com.foodchain.traceability_context.application.outbound.batches.BatchQueryService;
import com.foodchain.traceability_context.domain.model.entities.TraceabilityEvent;
import com.foodchain.traceability_context.domain.model.queries.GetHistoryByBatchIdQuery;
import com.foodchain.traceability_context.domain.model.queries.GetTraceabilityEventsByBatchIdQuery;
import com.foodchain.traceability_context.domain.repository.TraceabilityRepository;
import com.foodchain.traceability_context.domain.services.TraceabilityQueryService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TraceabilityQueryServiceImpl implements TraceabilityQueryService {
    private final TraceabilityRepository traceabilityRepository;
    private final BatchQueryService batchQueryService; // El cliente que habla con batch-management


    public TraceabilityQueryServiceImpl(TraceabilityRepository traceabilityRepository, BatchQueryService b) {
        this.traceabilityRepository = traceabilityRepository;
        this.batchQueryService = b;

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
}