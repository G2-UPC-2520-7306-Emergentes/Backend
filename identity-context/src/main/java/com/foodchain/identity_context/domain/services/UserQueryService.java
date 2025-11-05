// domain/services/UserQueryService.java
package com.foodchain.identity_context.domain.services;

import com.foodchain.identity_context.domain.model.aggregates.User;
import com.foodchain.identity_context.domain.model.queries.GetAllUsersByEnterpriseIdQuery;
import com.foodchain.identity_context.domain.model.queries.GetUserByEmailQuery;
import com.foodchain.identity_context.domain.model.queries.GetUserByIdQuery;
import com.foodchain.identity_context.domain.model.queries.GetUsersByIdsQuery;

import java.util.List;
import java.util.Optional;

public interface UserQueryService {
    Optional<User> handle(GetUserByIdQuery query);
    Optional<User> handle(GetUserByEmailQuery query);
    List<User> handle(GetAllUsersByEnterpriseIdQuery query);
    List<User> handle(GetUsersByIdsQuery query);
}