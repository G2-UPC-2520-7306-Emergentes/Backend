// EN: traceability-context/infrastructure/outbound/iam/EnterpriseQueryServiceImpl.java
package com.foodchain.traceability_context.infrastructure.outbound.iam;

import com.foodchain.traceability_context.application.outbound.iam.EnterpriseQueryService;
import com.foodchain.traceability_context.application.outbound.iam.EnterpriseResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Arrays;

@Service
public class EnterpriseQueryServiceImpl implements EnterpriseQueryService {

    private final RestTemplate restTemplate;
    private final String iamEnterpriseBaseUrl;

    public EnterpriseQueryServiceImpl(RestTemplate restTemplate, // ¡Inyecta el bean, no crees uno nuevo!
                                      @Value("${api.clients.iam-service.enterprise-url}") String iamEnterpriseBaseUrl) {
        this.restTemplate = restTemplate;
        this.iamEnterpriseBaseUrl = iamEnterpriseBaseUrl;
    }

    @Override
    public Map<UUID, EnterpriseResource> getEnterprisesByIds(List<UUID> enterpriseIds) {
        if (enterpriseIds == null || enterpriseIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // La URL debería apuntar al endpoint que creamos en identity-context

        try {
            // Preparamos la petición HTTP con la lista de IDs en el cuerpo
            HttpEntity<List<UUID>> requestEntity = new HttpEntity<>(enterpriseIds);

            // Hacemos la llamada POST y esperamos una lista de EnterpriseResource
            ResponseEntity<EnterpriseResource[]> response = restTemplate.postForEntity(iamEnterpriseBaseUrl, requestEntity, EnterpriseResource[].class);

            if (response.getBody() == null) {
                return Collections.emptyMap();
            }

            // Usamos Streams para convertir el array de respuesta en un Map de forma segura y eficiente.
            // Function.identity() significa que el valor del mapa será el objeto EnterpriseResource completo.
            return Arrays.stream(response.getBody())
                    .collect(Collectors.toMap(EnterpriseResource::enterpriseId, Function.identity()));

        } catch (Exception e) {
            System.err.println("Error al obtener detalles de empresas desde el IAM service: " + e.getMessage());
            // Devolvemos un mapa vacío para no romper el flujo principal, pero el error queda registrado.
            return Collections.emptyMap();
        }
    }
}