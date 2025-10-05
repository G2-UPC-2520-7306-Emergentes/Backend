// domain/model/entities/Role.java
package com.foodchain.identity_context.domain.model.entities;

import com.foodchain.identity_context.domain.model.valueobjects.Roles;
import jakarta.persistence.*; // Importamos JPA
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity // Le dice a JPA que esta clase es una tabla
@Table(name = "roles") // Nombramos la tabla
@Getter
@NoArgsConstructor
public class Role {

    @Id // Marca la clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Autogenerada por la base de datos
    private Long id;

    @Enumerated(EnumType.STRING) // Guarda el enum como "ROLE_USER" en lugar de un número
    @Column(length = 50, unique = true, nullable = false)
    private Roles name;

    public Role(Roles name) {
        this.name = name;
    }
}