// interfaces/rest/resources/CreateBatchResource.java
package com.foodchain.batch_management_context.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record CreateBatchResource(
        @NotBlank String productDescription,
        UUID enterpriseId // Lo obtendremos del token, pero lo dejamos por si se necesita explícitamente
) {}