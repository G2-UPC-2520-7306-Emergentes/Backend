// interfaces/rest/resources/EditBatchResource.java
package com.foodchain.batch_management_context.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

public record EditBatchResource(@NotBlank String productDescription) {}