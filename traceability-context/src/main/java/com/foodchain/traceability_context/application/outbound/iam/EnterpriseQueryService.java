package com.foodchain.traceability_context.application.outbound.iam;

import java.util.List;
import java.util.Map;
import java.util.UUID;

// EN: traceability-context/application/outbound/iam/EnterpriseQueryService.java
public interface EnterpriseQueryService {
    Map<UUID, EnterpriseResource> getEnterprisesByIds(List<UUID> enterpriseIds);
}