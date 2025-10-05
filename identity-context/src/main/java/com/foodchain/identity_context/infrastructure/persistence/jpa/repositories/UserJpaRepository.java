// infrastructure/persistence/jpa/repositories/UserJpaRepository.java
package com.foodchain.identity_context.infrastructure.persistence.jpa.repositories;

import com.foodchain.identity_context.domain.model.aggregates.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository // Le dice a Spring que esta es una interfaz de repositorio
public interface UserJpaRepository extends JpaRepository<User, UUID> {
    // Spring Data JPA creará automáticamente la implementación de estos métodos
    // basándose en el nombre. ¡No necesitamos escribir el SQL!
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}