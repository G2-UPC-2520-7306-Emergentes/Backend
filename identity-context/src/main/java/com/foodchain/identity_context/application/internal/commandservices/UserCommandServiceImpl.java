// application/internal/commandservices/UserCommandServiceImpl.java
package com.foodchain.identity_context.application.internal.commandservices;

import com.foodchain.identity_context.application.outbound.hashing.HashingService;
import com.foodchain.identity_context.application.outbound.notifications.EmailService;
import com.foodchain.identity_context.application.outbound.tokens.TokenService;
import com.foodchain.identity_context.domain.model.aggregates.User;
import com.foodchain.identity_context.domain.model.commands.*;
import com.foodchain.identity_context.domain.model.aggregates.Role;
import com.foodchain.identity_context.domain.model.entities.PasswordResetToken;
import com.foodchain.identity_context.domain.model.valueobjects.ERole;
import com.foodchain.identity_context.domain.repositories.PasswordResetTokenRepository;
import com.foodchain.identity_context.domain.repositories.RoleRepository;
import com.foodchain.identity_context.domain.repositories.UserRepository;
import com.foodchain.identity_context.domain.services.UserCommandService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final HashingService hashingService;
    private final RoleRepository roleRepository;
    private final TokenService tokenService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;



    public UserCommandServiceImpl(UserRepository userRepository, HashingService hashingService, TokenService tokenService, RoleRepository roleRepository, PasswordResetTokenRepository passwordResetTokenRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.hashingService = hashingService;
        this.roleRepository = roleRepository;
        this.tokenService = tokenService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public UUID handle(SignUpCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new IllegalStateException("User with email " + command.email() + " already exists.");
        }

        var hashedPassword = hashingService.encode(command.password());
        var user = User.register(command.enterpriseId(), command.email(), hashedPassword);

        // 1. Busca el rol por defecto en la base de datos.
        var defaultRole = roleRepository.findByName(ERole.ROLE_ENTERPRISE_USER)
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

    @Override
    @Transactional
    public void handle(AssignUserRoleCommand command) {
        // 1. Buscar el rol en la base de datos
        ERole newRoleEnum = ERole.valueOf(command.roleName());
        Role newRole = roleRepository.findByName(newRoleEnum)
                .orElseThrow(() -> new IllegalArgumentException("El rol especificado no existe: " + command.roleName()));

        // 2. Buscar el usuario
        var user = userRepository.findById(command.userId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + command.userId()));

        // 3. Delegar la lógica de negocio al método del agregado
        user.assignRoles(Set.of(newRole));

        // 4. Persistir los cambios
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void handle(RequestPasswordResetCommand command) {
        var user = userRepository.findByEmail(command.email())
                .orElseThrow(() -> new EntityNotFoundException("No se encontró usuario con el email: " + command.email()));

        var resetToken = new PasswordResetToken(user);
        passwordResetTokenRepository.save(resetToken);
        emailService.sendPasswordResetEmail(user.getEmail(), resetToken.getToken());
    }

    @Override
    @Transactional
    public void handle(ResetPasswordCommand command) {
        var resetToken = passwordResetTokenRepository.findByToken(command.token())
                .orElseThrow(() -> new IllegalArgumentException("Token de reseteo inválido."));

        if (resetToken.isExpired()) {
            passwordResetTokenRepository.delete(resetToken);
            throw new IllegalArgumentException("El token de reseteo ha expirado.");
        }

        var user = resetToken.getUser();
        user.updatePassword(hashingService.encode(command.newPassword()));
        userRepository.save(user);

        // El token ya se usó, lo eliminamos.
        passwordResetTokenRepository.delete(resetToken);
    }
}