package com.foodchain.batch_management_context.domain.model.queries;

import com.foodchain.batch_management_context.domain.model.valueobjects.BatchId;

public record GetBatchByIdQuery(
        BatchId batchId) {
}
