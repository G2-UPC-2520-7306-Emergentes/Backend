// domain/repository/UserRepository.java
package com.foodchain.identity_context.domain.repositories;

import com.foodchain.identity_context.domain.model.aggregates.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    void save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}