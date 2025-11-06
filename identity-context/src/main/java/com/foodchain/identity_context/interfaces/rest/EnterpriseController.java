// EN: identity-context/interfaces/rest/EnterpriseController.java
package com.foodchain.identity_context.interfaces.rest;

import com.foodchain.identity_context.interfaces.rest.resources.EnterpriseResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/iam/enterprises")
@Tag(name = "Enterprises", description = "API para la consulta de información pública de empresas.")
public class EnterpriseController {

    @Operation(summary = "Obtener información pública de una empresa", description = "Devuelve los detalles públicos de una empresa por su ID.")
    @GetMapping("/{enterpriseId}")
    public ResponseEntity<EnterpriseResource> getEnterpriseDetails(@PathVariable UUID enterpriseId) {
        // --- SIMULACIÓN PARA DESARROLLO ---
        // TODO: En un sprint futuro, esta información vendría de una entidad Enterprise en la base de datos.
        // Por ahora, devolvemos datos de ejemplo (mock).

        System.out.println("ADVERTENCIA: Devolviendo datos simulados para la empresa con ID: " + enterpriseId);

        var mockEnterprise = new EnterpriseResource(
                enterpriseId,
                "Café Altomayo",
                "https://altomayo.com.pe/wp-content/uploads/2021/05/logo-altomayo.png",
                List.of("Certificación Orgánica", "Comercio Justo")
        );

        return ResponseEntity.ok(mockEnterprise);
    }
}