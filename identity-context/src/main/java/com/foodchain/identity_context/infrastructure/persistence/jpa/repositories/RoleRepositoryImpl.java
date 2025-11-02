package com.foodchain.identity_context.infrastructure.persistence.jpa.repositories;

import com.foodchain.identity_context.domain.model.aggregates.Role;
import com.foodchain.identity_context.domain.model.valueobjects.ERole;
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
    public Optional<Role> findByName(ERole name) {
        return roleJpaRepository.findByName(name);
    }

    @Override
    public void save(Role role) {
        roleJpaRepository.save(role);
    }

    @Override
    public boolean existsByName(ERole name) {
        return roleJpaRepository.existsByName(name);
    }
}