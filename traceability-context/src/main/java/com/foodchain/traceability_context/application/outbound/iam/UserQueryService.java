// application/outbound/iam/UserQueryService.java
package com.foodchain.traceability_context.application.outbound.iam;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface UserQueryService {
    Map<UUID, String> getUsernamesForIds(List<UUID> userIds);
}