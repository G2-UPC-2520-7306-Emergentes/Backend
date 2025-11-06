package com.foodchain.traceability_context.domain.model.queries;

import java.util.UUID;

// domain/model/queries/GetPublicTraceabilityEventsByBatchIdQuery.java
public record GetPublicTraceabilityEventsByBatchIdQuery(UUID batchId) {}