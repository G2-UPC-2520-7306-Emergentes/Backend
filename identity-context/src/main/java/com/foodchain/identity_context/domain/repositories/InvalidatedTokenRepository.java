// domain/repository/InvalidatedTokenRepository.java
package com.foodchain.identity_context.domain.repositories;

import com.foodchain.identity_context.domain.model.entities.InvalidatedToken;
import java.util.Optional;

public interface InvalidatedTokenRepository {
    void save(InvalidatedToken token);
    Optional<InvalidatedToken> findByTokenId(String tokenId);
}