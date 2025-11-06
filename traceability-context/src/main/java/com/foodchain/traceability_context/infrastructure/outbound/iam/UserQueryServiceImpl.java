// EN: traceability-context/infrastructure/outbound/iam/UserQueryServiceImpl.java
package com.foodchain.traceability_context.infrastructure.outbound.iam;

import com.foodchain.shared_domain.domain.model.aggregates.UserDetails;
import com.foodchain.traceability_context.application.outbound.iam.UserQueryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserQueryServiceImpl implements UserQueryService {

    private final RestTemplate restTemplate;
    private final String iamUserBaseUrl;

    public UserQueryServiceImpl(RestTemplate restTemplate,
                                @Value("${api.clients.iam-service.user-url}") String iamUserBaseUrl) {
        this.restTemplate = restTemplate;
        this.iamUserBaseUrl = iamUserBaseUrl;
    }

    @Override
    public Map<UUID, UserDetails> getUserDetailsForIds(List<UUID> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        String url = iamUserBaseUrl + "/batch-details";

        try {
            HttpEntity<List<UUID>> requestEntity = new HttpEntity<>(userIds);

            // Usamos postForObject y esperamos un array de UserDetails
            UserDetails[] response = restTemplate.postForObject(url, requestEntity, UserDetails[].class);

            if (response == null) {
                return Collections.emptyMap();
            }

            // Convertimos el array a un mapa
            return Arrays.stream(response)
                    .collect(Collectors.toMap(UserDetails::userId, Function.identity()));

        } catch (Exception e) {
            System.err.println("Error al obtener detalles de usuarios desde el IAM service: " + e.getMessage());
            return Collections.emptyMap();
        }
    }
}