// interfaces/rest/resources/CreateBatchResource.java
package com.foodchain.batch_management_context.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

public record CreateBatchResource(
        @NotBlank String productDescription) {}