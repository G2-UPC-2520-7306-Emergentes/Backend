// infrastructure/persistence/jpa/repositories/UserRepositoryImpl.java
package com.foodchain.identity_context.infrastructure.persistence.jpa.repositories;

import com.foodchain.identity_context.domain.model.aggregates.User;
import com.foodchain.identity_context.domain.repositories.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component // Marcamos esta clase como un bean de Spring
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    // Inyectamos la interfaz de Spring Data JPA
    public UserRepositoryImpl(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public void save(User user) {
        userJpaRepository.save(user); // Simplemente delegamos la llamada
    }
    @Override
    public Optional<User> findById(UUID id) {
        return userJpaRepository.findById(id);
    }
    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email);
    }
    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }
    @Override
    public List<User> findAllByEnterpriseId(UUID enterpriseId) {
        return userJpaRepository.findAllByEnterpriseId(enterpriseId);
    }
    @Override
    public List<User> findAllById(List<UUID> ids){
        return userJpaRepository.findAllById(ids);
    }
}