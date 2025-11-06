// EN: identity-context/interfaces/rest/resources/EnterpriseResource.java
package com.foodchain.identity_context.interfaces.rest.resources;

import java.util.List;
import java.util.UUID;

/**
 * DTO para la información pública de una empresa.
 */
public record EnterpriseResource(
        UUID enterpriseId,
        String name,
        String logoUrl,
        List<String> certifications
) {}