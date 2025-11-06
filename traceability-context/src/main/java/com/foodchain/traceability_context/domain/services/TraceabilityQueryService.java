// EN: traceability-context/src/main/java/com/foodchain/traceability_context/domain/services/TraceabilityQueryService.java
package com.foodchain.traceability_context.domain.services;

import com.foodchain.traceability_context.domain.model.entities.TraceabilityEvent;
import com.foodchain.traceability_context.domain.model.queries.GetPublicTraceabilityEventsByBatchIdQuery;
import com.foodchain.traceability_context.domain.model.queries.GetTraceabilityEventsByBatchIdQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TraceabilityQueryService {
    Page<TraceabilityEvent> handle(GetTraceabilityEventsByBatchIdQuery query, Pageable pageable);
    Page<TraceabilityEvent> handle(GetPublicTraceabilityEventsByBatchIdQuery query, Pageable pageable);
    List<TraceabilityEvent> handle(GetPublicTraceabilityEventsByBatchIdQuery query);
}