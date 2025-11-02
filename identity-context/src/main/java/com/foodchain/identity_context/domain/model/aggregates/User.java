// domain/model/aggregates/User.java
package com.foodchain.identity_context.domain.model.aggregates;

import com.foodchain.shared_domain.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * AGGREGATE ROOT: User
 * Es la raíz del agregado de identidad y el único punto de entrada para modificar su estado.
 * Protege las invariantes (reglas de negocio) del dominio.
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor // Requerido por JPA
public class User extends AuditableAbstractAggregateRoot<User> {

    @Id // La clave primaria de nuestra entidad
    private UUID id;

    @Column(nullable = false)
    private UUID enterpriseId;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)   // Almacenamos la contraseña hasheada
    private String password;

    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE }) // Un usuario puede tener muchos roles
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    private boolean isActive;

    /**
     * Constructor privado para forzar la creación a través del factory method.
     * Asegura que cada instancia de User sea válida desde su creación.
     */
    private User(UUID enterpriseId, String email, String password) {
        this.id = UUID.randomUUID();
        this.enterpriseId = enterpriseId;
        this.email = email;
        this.password = password;
        this.isActive = true;
        this.roles = new HashSet<>();
    }

    /**
     * FACTORY METHOD: Punto de entrada controlado para la creación de un User.
     * @return Una instancia válida de User.
     */
    public static User register(UUID enterpriseId, String email, String password) {
        // Validación de precondiciones (Guard Clauses)
        if (enterpriseId == null || Strings.isBlank(email) || Strings.isBlank(password)) {
            throw new IllegalArgumentException("Enterprise ID, email, and password are required for registration.");
        }
        return new User(enterpriseId, email, password);
    }

    /**
     * MÉTODO DE NEGOCIO: Asigna un nuevo rol al usuario.
     * La lógica para añadir un rol está encapsulada aquí.
     */
    public void assignRole(Role role) {
        this.roles.add(role);
    }

    /**
     * MÉTODO DE NEGOCIO: Obtiene los nombres de los roles asignados al usuario como un conjunto de cadenas.
     * @return Un conjunto de nombres de roles.
     */
    public Set<String> getRoleStrings() {
        return this.roles.stream().map(role -> role.getName().name()).collect(Collectors.toSet());
    }

    /**
     * MÉTODO DE NEGOCIO: Asigna un nuevo conjunto de roles al usuario.
     * Para nuestro sistema, reemplazará el rol existente por el nuevo.
     * @param newRoles El conjunto de nuevos roles a asignar.
     */
    public void assignRoles(Set<Role> newRoles) {
        if (newRoles == null || newRoles.isEmpty()) {
            throw new IllegalArgumentException("El conjunto de roles no puede ser nulo o vacío.");
        }
        this.roles.clear();
        this.roles.addAll(newRoles);
    }

    public void updatePassword(String newHashedPassword) {
        this.password = newHashedPassword;
    }
}