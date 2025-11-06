// infrastructure/persistence/jpa/repositories/InvalidatedTokenRepositoryImpl.java
package com.foodchain.identity_context.infrastructure.persistence.jpa.repositories;

import com.foodchain.identity_context.domain.model.entities.InvalidatedToken;
import com.foodchain.identity_context.domain.repositories.InvalidatedTokenRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class InvalidatedTokenRepositoryImpl implements InvalidatedTokenRepository {
    private final InvalidatedTokenJpaRepository jpaRepository;

    public InvalidatedTokenRepositoryImpl(InvalidatedTokenJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(InvalidatedToken token) {
        jpaRepository.save(token);
    }

    @Override
    public Optional<InvalidatedToken> findByTokenId(String tokenId) {
        return jpaRepository.findByTokenId(tokenId);
    }
}