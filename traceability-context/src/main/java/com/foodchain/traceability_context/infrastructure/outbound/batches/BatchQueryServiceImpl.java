package com.foodchain.traceability_context.infrastructure.outbound.batches;

import com.foodchain.traceability_context.application.outbound.batches.BatchQueryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class BatchQueryServiceImpl implements BatchQueryService {

    private final RestTemplate restTemplate;

    // Suponiendo que tienes una URL configurada en application.yml
    @Value("${api.clients.batch-service.base-url}")
    private String batchServiceUrl;

    public BatchQueryServiceImpl() { this.restTemplate = new RestTemplate(); }

    private record BatchOwnerResource(UUID enterpriseId) {}

    @Override
    public boolean verifyBatchOwnership(UUID batchId, UUID expectedEnterpriseId) {
        try {
            String url = batchServiceUrl + "/" + batchId + "/owner";
            // TODO: Propagar el header de autenticación en la llamada
            ResponseEntity<BatchOwnerResource> response = restTemplate.getForEntity(url, BatchOwnerResource.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return expectedEnterpriseId.equals(response.getBody().enterpriseId());
            }
            return false;
        } catch (HttpClientErrorException.NotFound e) {
            return false; // El lote no existe, por lo tanto, no hay propiedad que verificar
        } catch (Exception e) {
            System.err.println("Error al verificar la propiedad del lote: " + e.getMessage());
            return false; // Por seguridad, si hay un error, denegamos el acceso.
        }
    }


}