package com.foodchain.identity_context.interfaces.events.handlers;

import com.foodchain.identity_context.domain.model.entities.Role;
import com.foodchain.identity_context.domain.model.valueobjects.Roles;
import com.foodchain.identity_context.domain.repositories.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
public class ApplicationReadyEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationReadyEventHandler.class);
    private final RoleRepository roleRepository;

    public ApplicationReadyEventHandler(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Este metodo se ejecuta automáticamente una vez que la aplicación está completamente iniciada.
     * Se encarga de "sembrar" los roles en la base de datos si no existen.
     */
    @EventListener
    public void on(ApplicationReadyEvent event) {
        LOGGER.info("Verificando si se necesita sembrar roles...");
        Arrays.stream(Roles.values()).forEach(roleEnum -> {
            if (!roleRepository.existsByName(roleEnum)) {
                roleRepository.save(new Role(roleEnum));
                LOGGER.info("Rol {} sembrado.", roleEnum.name());
            }
        });
        LOGGER.info("Verificación de roles finalizada.");
    }
}