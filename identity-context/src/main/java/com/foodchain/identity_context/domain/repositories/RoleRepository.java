package com.foodchain.identity_context.domain.repositories;

import com.foodchain.identity_context.domain.model.entities.Role;
import com.foodchain.identity_context.domain.model.valueobjects.Roles;
import java.util.Optional;

public interface RoleRepository {
    Optional<Role> findByName(Roles name);
    void save(Role role);
    boolean existsByName(Roles name);
}