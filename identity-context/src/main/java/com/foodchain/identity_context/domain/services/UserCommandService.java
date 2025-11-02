// domain/services/UserCommandService.java
package com.foodchain.identity_context.domain.services;

import com.foodchain.identity_context.domain.model.commands.AssignUserRoleCommand;
import com.foodchain.identity_context.domain.model.commands.SignInCommand;
import com.foodchain.identity_context.domain.model.commands.SignUpCommand;

import java.util.UUID;

public interface UserCommandService {
    UUID handle(SignUpCommand command);
    String handle(SignInCommand command); // Devuelve el token JWT
    void handle(AssignUserRoleCommand command);
}