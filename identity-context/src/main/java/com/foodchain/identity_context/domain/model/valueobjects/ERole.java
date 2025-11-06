// domain/model/valueobjects/ERole.java
package com.foodchain.identity_context.domain.model.valueobjects;

/**
 * VALUE OBJECT: ERole (Enum)
 * Define un conjunto fijo y seguro de los nombres de roles disponibles en todo el sistema.
 * Esto actúa como el "contrato" de dominio para los roles, asegurando que solo se puedan
 * usar valores válidos y predefinidos.
 * Sigue la convención de Spring Security de prefijar los roles con "ROLE_".
 */
public enum ERole {
    /**
     * Rol para usuarios estándar de una empresa.
     * Pueden crear lotes, registrar eventos, etc.
     */
    ROLE_ENTERPRISE_USER,

    /**
     * Rol para administradores de una empresa.
     * Pueden gestionar usuarios, asignar roles, y realizar todas las acciones de un USER.
     */
    ROLE_ENTERPRISE_ADMIN
}