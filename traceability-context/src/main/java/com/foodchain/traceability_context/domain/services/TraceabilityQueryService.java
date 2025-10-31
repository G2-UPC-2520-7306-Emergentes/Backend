// domain/services/TraceabilityQueryService.java
package com.foodchain.traceability_context.domain.services;

import com.foodchain.traceability_context.domain.model.entities.TraceabilityEvent;
import com.foodchain.traceability_context.domain.model.queries.GetHistoryByBatchIdQuery;
import java.util.List;

public interface TraceabilityQueryService {
    List<TraceabilityEvent> handle(GetHistoryByBatchIdQuery query);
}