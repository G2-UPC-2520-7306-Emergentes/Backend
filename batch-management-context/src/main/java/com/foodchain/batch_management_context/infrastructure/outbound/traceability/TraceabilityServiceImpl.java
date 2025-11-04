// EN: batch-management-context/src/main/java/com/foodchain/batch_management_context/infrastructure/outbound/traceability/TraceabilityServiceImpl.java
package com.foodchain.batch_management_context.infrastructure.outbound.traceability;

import com.foodchain.batch_management_context.application.outbound.traceability.TraceabilityService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

/**
 * ADAPTADOR DE SALIDA (Outbound Adapter)
 * Implementación real del TraceabilityService que se comunica con el
 * traceability-context a través de una API REST para verificar si un lote
 * tiene eventos registrados.
 */
@Service
public class TraceabilityServiceImpl implements TraceabilityService {

    private final RestTemplate restTemplate;
    private final String traceabilityServiceUrl;

    public TraceabilityServiceImpl(RestTemplate restTemplate, // Could not autowire. No beans of 'RestTemplate' type found.
                                   @Value("${api.clients.traceability-service.base-url}") String traceabilityServiceUrl) {
        this.restTemplate = restTemplate;
        this.traceabilityServiceUrl = traceabilityServiceUrl;
    }

    @Override
    public boolean hasTraceabilityEvents(UUID batchId) {
        // 1. Construimos la URL completa para el endpoint que consulta eventos por lote.
        String url = traceabilityServiceUrl + "/batch/" + batchId;

        try {
            // 2. Realizamos la llamada HTTP GET.
            // Usamos ParameterizedTypeReference para que RestTemplate sepa cómo deserializar
            // una lista de objetos genéricos, aunque en este caso solo nos interesa si la lista no está vacía.
            // TODO: En el futuro, el traceability-context debería exponer un endpoint más eficiente
            // como /batch/{batchId}/count o /batch/{batchId}/exists que devuelva solo un número o un booleano.
            ResponseEntity<List<Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null, // No necesitamos un cuerpo (body) ni headers especiales por ahora
                    new ParameterizedTypeReference<>() {
                    }
            );

            // 3. Analizamos la respuesta.
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Si la llamada fue exitosa y el cuerpo no es nulo,
                // verificamos si la lista tiene al menos un elemento.
                return !response.getBody().isEmpty();
            }

            // Si la respuesta no fue exitosa pero no lanzó una excepción (raro), devolvemos false por seguridad.
            return false;

        } catch (HttpClientErrorException e) {
            // 4. Manejamos los errores esperados.
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                // Si el traceability-context devuelve 404, significa que el lote no se encontró,
                // por lo tanto no tiene eventos.
                return false;
            }
            if (e.getStatusCode() == HttpStatus.FORBIDDEN || e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                // Si nos deniega el acceso, algo está mal en la comunicación entre servicios.
                // Por seguridad, asumimos que no podemos eliminar y lanzamos un error.
                System.err.println("Error de autorización al llamar al servicio de trazabilidad.");
                throw new SecurityException("No se pudo verificar el historial del lote debido a un problema de permisos entre servicios.");
            }
            // Para otros errores del cliente, lo registramos y asumimos que no podemos eliminar.
            System.err.println("Error del cliente al llamar al servicio de trazabilidad: " + e.getMessage());
            return true; // Devolvemos true para prevenir la eliminación por seguridad.

        } catch (Exception e) {
            // 5. Manejamos errores inesperados (ej. el servicio está caído).
            System.err.println("Error inesperado al conectar con el servicio de trazabilidad: " + e.getMessage());
            // Por seguridad, si no podemos verificar, no permitimos la eliminación.
            // Devolver 'true' previene la acción de borrado.
            return true;
        }
    }
}