// interfaces/rest/transform/BatchResourceFromEntityAssembler.java
package com.foodchain.batch_management_context.interfaces.rest.transform;

import com.foodchain.batch_management_context.domain.model.aggregates.Batch;
import com.foodchain.batch_management_context.interfaces.rest.resources.BatchResource;

public class BatchResourceFromEntityAssembler {
    public static BatchResource toResourceFromEntity(Batch entity) {
        return new BatchResource(
                entity.getBatchId().getValue(),
                entity.getProductDescription(),
                entity.getStatus().name(), // .name() convierte el enum a String
                entity.getCreationDate()
        );
    }
}