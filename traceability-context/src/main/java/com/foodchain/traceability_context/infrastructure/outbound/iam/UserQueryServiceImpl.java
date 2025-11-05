package com.foodchain.traceability_context.infrastructure.outbound.iam;

import com.foodchain.traceability_context.application.outbound.iam.UserQueryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserQueryServiceImpl implements UserQueryService {
    private final RestTemplate restTemplate;
    private final String iamServiceUrl;

    public UserQueryServiceImpl(@Value("${api.clients.iam-service.query-url}") String iamServiceUrl) {
        this.restTemplate = new RestTemplate();
        this.iamServiceUrl = iamServiceUrl;
    }

    private record UserBatchDetailsResource(UUID id, String email) {}

    @Override
    public Map<UUID, String> getUsernamesForIds(List<UUID> userIds) {
        String url = iamServiceUrl + "/users/batch-details";
        try {
            // TODO: Propagar el header de autenticación
            ResponseEntity<List<UserBatchDetailsResource>> response = restTemplate.exchange(url, HttpMethod.POST,
                    new HttpEntity<>(userIds), new ParameterizedTypeReference<>() {});

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody().stream()
                        .collect(Collectors.toMap(UserBatchDetailsResource::id, UserBatchDetailsResource::email));
            }
        } catch (Exception e) {
            System.err.println("Error al obtener detalles de usuarios: " + e.getMessage());
        }
        return Collections.emptyMap();
    }
}