// domain/repository/PasswordResetTokenRepository.java
package com.foodchain.identity_context.domain.repositories;

import com.foodchain.identity_context.domain.model.entities.PasswordResetToken;
import java.util.Optional;

public interface PasswordResetTokenRepository {
    void save(PasswordResetToken passwordResetToken);
    Optional<PasswordResetToken> findByToken(String token);
    void delete(PasswordResetToken passwordResetToken);
}