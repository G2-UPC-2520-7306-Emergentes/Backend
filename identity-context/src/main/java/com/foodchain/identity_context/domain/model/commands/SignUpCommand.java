// domain/model/commands/SignUpCommand.java
package com.foodchain.identity_context.domain.model.commands;

import java.util.UUID;

/**
 * Comando para registrar un nuevo usuario.
 * Es un DTO inmutable que representa la intención de un caso de uso.
 * @param enterpriseId El ID de la empresa a la que pertenece el usuario.
 * @param email El email del usuario, que será su identificador de login.
 * @param password La contraseña en texto plano, que será hasheada por la capa de aplicación.
 */
public record SignUpCommand(UUID enterpriseId, String email, String password) {
}