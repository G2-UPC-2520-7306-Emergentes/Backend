// domain/model/queries/GetAllUsersByEnterpriseIdQuery.java
package com.foodchain.identity_context.domain.model.queries;

import java.util.UUID;

public record GetAllUsersByEnterpriseIdQuery(UUID enterpriseId) {}