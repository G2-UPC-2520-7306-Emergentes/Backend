package com.foodchain.identity_context.infrastructure.persistence.jpa.repositories;

import com.foodchain.identity_context.domain.model.entities.Role;
import com.foodchain.identity_context.domain.model.valueobjects.Roles;
import com.foodchain.identity_context.domain.repositories.RoleRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class RoleRepositoryImpl implements RoleRepository {
    private final RoleJpaRepository roleJpaRepository;

    public RoleRepositoryImpl(RoleJpaRepository roleJpaRepository) {
        this.roleJpaRepository = roleJpaRepository;
    }

    @Override
    public Optional<Role> findByName(Roles name) {
        return roleJpaRepository.findByName(name);
    }

    @Override
    public void save(Role role) {
        roleJpaRepository.save(role);
    }

    @Override
    public boolean existsByName(Roles name) {
        return roleJpaRepository.existsByName(name);
    }
}