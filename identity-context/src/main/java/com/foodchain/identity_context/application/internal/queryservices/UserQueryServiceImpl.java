// application/internal/queryservices/UserQueryServiceImpl.java
package com.foodchain.identity_context.application.internal.queryservices;

import com.foodchain.identity_context.domain.model.aggregates.User;
import com.foodchain.identity_context.domain.model.queries.GetAllUsersByEnterpriseIdQuery;
import com.foodchain.identity_context.domain.model.queries.GetUserByEmailQuery;
import com.foodchain.identity_context.domain.model.queries.GetUserByIdQuery;
import com.foodchain.identity_context.domain.repositories.UserRepository;
import com.foodchain.identity_context.domain.services.UserQueryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserQueryServiceImpl implements UserQueryService {
    private final UserRepository userRepository;

    public UserQueryServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> handle(GetUserByIdQuery query) {
        return userRepository.findById(query.userId());
    }

    @Override
    public Optional<User> handle(GetUserByEmailQuery query) {
        return userRepository.findByEmail(query.email());
    }

    @Override
    public List<User> handle(GetAllUsersByEnterpriseIdQuery query) {
        return userRepository.findAllByEnterpriseId(query.enterpriseId());
    }

}