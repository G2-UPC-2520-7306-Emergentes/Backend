// infrastructure/persistence/jpa/repositories/UserJpaRepository.java
package com.foodchain.identity_context.infrastructure.persistence.jpa.repositories;

import com.foodchain.identity_context.domain.model.aggregates.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository // Le dice a Spring que esta es una interfaz de repositorio
public interface UserJpaRepository extends JpaRepository<User, UUID> {
    // Spring Data JPA creará automáticamente la implementación de estos métodos
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findAllByEnterpriseId(UUID enterpriseId);
}