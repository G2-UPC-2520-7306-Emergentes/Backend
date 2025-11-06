package com.foodchain.traceability_context.domain.model.queries;

import java.util.UUID;

public record GetTraceabilityEventsByBatchIdQuery(UUID batchId, UUID enterpriseId) {
}
