// domain/model/aggregates/Role.java
package com.foodchain.identity_context.domain.model.aggregates;

import com.foodchain.identity_context.domain.model.valueobjects.ERole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * AGGREGATE PART: Role (Entity)
 *
 * Representa un rol en la base de datos, mapeando el enum ERole a una entidad persistente.
 * Esta entidad se asociará a los usuarios en una relación Many-to-Many.
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Almacena el nombre del rol.
     * Usamos @Enumerated(EnumType.STRING) para guardar el nombre del enum ("ROLE_ENTERPRISE_USER")
     * en la base de datos, lo cual es mucho más legible que guardar su índice numérico (0, 1).
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false, unique = true)
    private ERole name;

    public Role(ERole name) {
        this.name = name;
    }
}