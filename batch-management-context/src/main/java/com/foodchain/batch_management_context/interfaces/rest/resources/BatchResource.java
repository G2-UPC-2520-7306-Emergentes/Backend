// interfaces/rest/resources/BatchResource.java
package com.foodchain.batch_management_context.interfaces.rest.resources;

import java.util.Date;
import java.util.UUID;

public record BatchResource(
        UUID batchId,
        String productDescription,
        String status,
        Date creationDate
) {}