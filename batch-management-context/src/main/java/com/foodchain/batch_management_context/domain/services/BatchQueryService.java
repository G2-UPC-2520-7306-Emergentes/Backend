// domain/services/BatchQueryService.java
package com.foodchain.batch_management_context.domain.services;

import com.foodchain.batch_management_context.domain.model.aggregates.Batch;
import com.foodchain.batch_management_context.domain.model.queries.GetBatchByIdQuery;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BatchQueryService {
    List<Batch> handle(UUID enterpriseId);
    Optional<Batch> handle(GetBatchByIdQuery query);
}
