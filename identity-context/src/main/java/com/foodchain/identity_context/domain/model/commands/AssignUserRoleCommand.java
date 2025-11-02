// domain/model/commands/AssignUserRoleCommand.java
package com.foodchain.identity_context.domain.model.commands;

import java.util.UUID;

/**
 * Comando para asignar un nuevo rol a un usuario existente.
 * @param userId El ID del usuario a modificar.
 * @param roleName El nombre del nuevo rol a asignar (ej. "ROLE_ENTERPRISE_ADMIN").
 */
public record AssignUserRoleCommand(UUID userId, String roleName) {}