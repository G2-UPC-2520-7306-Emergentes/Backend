// domain/services/UserCommandService.java
package com.foodchain.identity_context.domain.services;

import com.foodchain.identity_context.domain.model.commands.*;

import java.util.UUID;

public interface UserCommandService {
    UUID handle(SignUpCommand command);
    String handle(SignInCommand command); // Devuelve el token JWT
    void handle(AssignUserRoleCommand command);
    void handle(RequestPasswordResetCommand command);
    void handle(ResetPasswordCommand command);
}