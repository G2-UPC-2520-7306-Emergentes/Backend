// infrastructure/outbound/iam/IamServiceImpl.java
package com.foodchain.batch_management_context.infrastructure.outbound.iam;

import com.foodchain.batch_management_context.application.outbound.iam.IamService;
import com.foodchain.batch_management_context.application.outbound.iam.UserDetails;
import org.springframework.beans.factory.annotation.Value; // Importa @Value
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Optional;

@Service
public class IamServiceImpl implements IamService {

    private final RestTemplate restTemplate;
    private final String iamServiceBaseUrl; // Variable para guardar la URL

    public IamServiceImpl(@Value("${api.clients.iam-service.base-url}") String iamServiceBaseUrl) {
        this.restTemplate = new RestTemplate();
        this.iamServiceBaseUrl = iamServiceBaseUrl;
    }


    @Override
    public Optional<UserDetails> validateTokenAndGetUserDetails(String token) {
        // La URL del endpoint de validación en el identity-context
        String validationUrl = iamServiceBaseUrl + "/validate";

        try {
            // Hacemos una petición POST al identity-service, enviando el token
            // y esperando recibir los UserDetails de vuelta.
            UserDetails userDetails = restTemplate.postForObject(validationUrl, token, UserDetails.class);
            return Optional.ofNullable(userDetails);
        } catch (Exception e) {
            // Si la llamada falla (ej. 401 Unauthorized, servicio caído), el token no es válido.
            return Optional.empty();
        }
    }
}