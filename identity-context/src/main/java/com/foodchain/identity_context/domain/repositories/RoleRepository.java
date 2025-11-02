package com.foodchain.identity_context.domain.repositories;

import com.foodchain.identity_context.domain.model.aggregates.Role;
import com.foodchain.identity_context.domain.model.valueobjects.ERole;

import java.util.Optional;

public interface RoleRepository {
    Optional<Role> findByName(ERole name);
    void save(Role role);
    boolean existsByName(ERole name);
}