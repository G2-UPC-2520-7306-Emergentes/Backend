// infrastructure/persistence/jpa/repositories/InvalidatedTokenJpaRepository.java
package com.foodchain.identity_context.infrastructure.persistence.jpa.repositories;

import com.foodchain.identity_context.domain.model.entities.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface InvalidatedTokenJpaRepository extends JpaRepository<InvalidatedToken, Long> {
    Optional<InvalidatedToken> findByTokenId(String tokenId);
}