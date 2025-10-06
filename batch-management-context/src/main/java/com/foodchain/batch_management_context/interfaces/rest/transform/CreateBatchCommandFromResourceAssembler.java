// interfaces/rest/transform/CreateBatchCommandFromResourceAssembler.java
package com.foodchain.batch_management_context.interfaces.rest.transform;

import com.foodchain.batch_management_context.domain.model.commands.CreateBatchCommand;
import com.foodchain.batch_management_context.interfaces.rest.resources.CreateBatchResource;

public class CreateBatchCommandFromResourceAssembler {
    public static CreateBatchCommand toCommandFromResource(CreateBatchResource resource) {
        return new CreateBatchCommand(resource.enterpriseId(), resource.productDescription());
    }
}