// infrastructure/persistence/jpa/repositories/PasswordResetTokenRepositoryImpl.java
package com.foodchain.identity_context.infrastructure.persistence.jpa.repositories;

import com.foodchain.identity_context.domain.model.entities.PasswordResetToken;
import com.foodchain.identity_context.domain.repositories.PasswordResetTokenRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class PasswordResetTokenRepositoryImpl implements PasswordResetTokenRepository {

    private final PasswordResetTokenJpaRepository jpaRepository;

    public PasswordResetTokenRepositoryImpl(PasswordResetTokenJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(PasswordResetToken passwordResetToken) {
        jpaRepository.save(passwordResetToken);
    }

    @Override
    public Optional<PasswordResetToken> findByToken(String token) {
        return jpaRepository.findByToken(token);
    }

    @Override
    public void delete(PasswordResetToken passwordResetToken) {
        jpaRepository.delete(passwordResetToken);
    }
}