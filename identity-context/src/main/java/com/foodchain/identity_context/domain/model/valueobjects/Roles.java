// domain/model/valueobjects/Roles.java
package com.foodchain.identity_context.domain.model.valueobjects;

/**
 * Enumeración que define los roles disponibles en el sistema.
 * Al ser un Value Object, su igualdad se basa en su valor, no en su identidad.
 */
public enum Roles {
    ROLE_ENTERPRISE_USER,
    ROLE_ENTERPRISE_ADMIN
}