// domain/services/BatchCommandService.java
package com.foodchain.batch_management_context.domain.services;

import com.foodchain.batch_management_context.domain.model.commands.*;
import com.foodchain.batch_management_context.domain.model.valueobjects.BatchId;

public interface BatchCommandService {
    BatchId handle(CreateBatchCommand command);
    void handle(EditBatchCommand command);
    BatchId handle(DuplicateBatchCommand command);
    void handle(DeleteBatchCommand command);
    void handle(AssignImageToBatchCommand command);
}