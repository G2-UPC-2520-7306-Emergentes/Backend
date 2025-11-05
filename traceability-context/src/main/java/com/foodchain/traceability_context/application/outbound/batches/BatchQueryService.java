// traceability-context/application/outbound/batches/BatchQueryService.java
package com.foodchain.traceability_context.application.outbound.batches;

import java.util.Optional;
import java.util.UUID;

public interface BatchQueryService {
    boolean verifyBatchOwnership(UUID batchId, UUID enterpriseId);
    Optional<String> getBatchStatus(UUID batchId);
}