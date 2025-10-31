// application/internal/queryservices/TraceabilityQueryServiceImpl.java
package com.foodchain.traceability_context.application.internal.queryservices;

import com.foodchain.traceability_context.domain.model.entities.TraceabilityEvent;
import com.foodchain.traceability_context.domain.model.queries.GetHistoryByBatchIdQuery;
import com.foodchain.traceability_context.domain.repository.TraceabilityRepository;
import com.foodchain.traceability_context.domain.services.TraceabilityQueryService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TraceabilityQueryServiceImpl implements TraceabilityQueryService {
    private final TraceabilityRepository traceabilityRepository;

    public TraceabilityQueryServiceImpl(TraceabilityRepository traceabilityRepository) {
        this.traceabilityRepository = traceabilityRepository;
    }

    @Override
    public List<TraceabilityEvent> handle(GetHistoryByBatchIdQuery query) {
        // Simplemente delegamos la búsqueda al repositorio
        return traceabilityRepository.findByBatchId(query.batchId());
    }
}