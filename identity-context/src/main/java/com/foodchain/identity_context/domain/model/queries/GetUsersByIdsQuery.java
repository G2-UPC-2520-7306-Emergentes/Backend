// domain/model/queries/GetUsersByIdsQuery.java
package com.foodchain.identity_context.domain.model.queries;
import java.util.List;
import java.util.UUID;

public record GetUsersByIdsQuery(List<UUID> userIds) {}