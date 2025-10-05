// application/internal/commandservices/UserCommandServiceImpl.java
package com.foodchain.identity_context.application.internal.commandservices;

import com.foodchain.identity_context.application.outbound.hashing.HashingService;
import com.foodchain.identity_context.application.outbound.tokens.TokenService;
import com.foodchain.identity_context.domain.model.aggregates.User;
import com.foodchain.identity_context.domain.model.commands.SignInCommand;
import com.foodchain.identity_context.domain.model.commands.SignUpCommand;
import com.foodchain.identity_context.domain.model.valueobjects.Roles;
import com.foodchain.identity_context.domain.repositories.RoleRepository;
import com.foodchain.identity_context.domain.repositories.UserRepository;
import com.foodchain.identity_context.domain.services.UserCommandService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final HashingService hashingService;
    private final RoleRepository roleRepository;
    private final TokenService tokenService;

    public UserCommandServiceImpl(UserRepository userRepository, HashingService hashingService, TokenService tokenService, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.hashingService = hashingService;
        this.roleRepository = roleRepository;
        this.tokenService = tokenService;
    }

    @Override
    @Transactional
    public UUID handle(SignUpCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new IllegalStateException("User with email " + command.email() + " already exists.");
        }

        var hashedPassword = hashingService.encode(command.password());
        var user = User.register(command.enterpriseId(), command.email(), hashedPassword);

        // ¡LÓGICA CORREGIDA!
        // 1. Busca el rol por defecto en la base de datos.
        var defaultRole = roleRepository.findByName(Roles.ROLE_ENTERPRISE_USER)
                .orElseThrow(() -> new IllegalStateException("Default role not found. Seeding might have failed."));

        // 2. Asigna el rol persistido (con ID) al nuevo usuario.
        user.assignRole(defaultRole);

        userRepository.save(user);
        return user.getId();
    }

    @Override
    @Transactional(readOnly = true) // Es una operación de solo lectura
    public String handle(SignInCommand command) {
        var user = userRepository.findByEmail(command.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

        if (!hashingService.matches(command.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        return tokenService.generateToken(user.getEmail());
    }
}