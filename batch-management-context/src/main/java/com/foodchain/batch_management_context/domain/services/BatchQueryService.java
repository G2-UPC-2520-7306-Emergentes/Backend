// domain/services/BatchQueryService.java
package com.foodchain.batch_management_context.domain.services;

import com.foodchain.batch_management_context.domain.model.aggregates.Batch;
import java.util.List;
import java.util.UUID;

public interface BatchQueryService {
    List<Batch> handle(UUID enterpriseId);
}
