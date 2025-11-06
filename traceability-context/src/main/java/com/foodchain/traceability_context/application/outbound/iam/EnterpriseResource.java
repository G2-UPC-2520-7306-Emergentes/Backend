package com.foodchain.traceability_context.application.outbound.iam;

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