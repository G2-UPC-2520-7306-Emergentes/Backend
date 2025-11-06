// EN: traceability-context/src/main/java/com/foodchain/traceability_context/interfaces/rest/transform/TraceabilityEventResourceFromEntityAssembler.java
package com.foodchain.traceability_context.interfaces.rest.transform;

import com.foodchain.traceability_context.application.outbound.iam.EnterpriseResource;
import com.foodchain.traceability_context.domain.model.entities.TraceabilityEvent;
import com.foodchain.traceability_context.interfaces.rest.resources.TraceabilityEventResource;

/**
 * Assembler (o Mapper) responsable de convertir la entidad de dominio `TraceabilityEvent`
 * en el DTO de recurso `TraceabilityEventResource` que se expone en la API.
 * Esta clase desacopla la representación interna del dominio de la representación pública de la API.
 */
public class TraceabilityEventResourceFromEntityAssembler {

    /**
     * Método de mapeo enriquecido.
     * Convierte la entidad y la enriquece con datos externos como el nombre del actor y la URL de verificación.
     *
     * @param entity La entidad de dominio `TraceabilityEvent` a convertir.
     * @param actorName El nombre (o email) del actor, obtenido desde el identity-context.
     * @param txUrlTemplate La plantilla de URL para el explorador de blockchain (ej. "https://explorer.com/tx/{txHash}").
     * @return El DTO `TraceabilityEventResource` listo para ser serializado a JSON.
     */
    public static TraceabilityEventResource toResourceFromEntity(TraceabilityEvent entity, String actorName,
                                                                 EnterpriseResource enterprise, String txUrlTemplate) {
        // 1. Mapear el Value Object anidado `Location` a su DTO de recurso `LocationResource`.
        TraceabilityEventResource.LocationResource locationResource = null;
        if (entity.getLocation() != null) {
            locationResource = new TraceabilityEventResource.LocationResource(
                    entity.getLocation().getLatitude(),
                    entity.getLocation().getLongitude(),
                    entity.getLocation().getAddress(),
                    entity.getLocation().getCity(),
                    entity.getLocation().getRegion(),
                    entity.getLocation().getCountry()
            );
        }

        // 2. Construir la URL de verificación solo si el evento ha sido confirmado en la blockchain.
        String verificationUrl = null;
        if (entity.getTransactionHash() != null && !entity.getTransactionHash().isBlank() && txUrlTemplate != null) {
            verificationUrl = txUrlTemplate.replace("{txHash}", entity.getTransactionHash());
        }

        // Mapeo del DTO de empresa
        TraceabilityEventResource.EnterpriseInfoResource enterpriseInfo = null;
        if (enterprise != null) {
            enterpriseInfo = new TraceabilityEventResource.EnterpriseInfoResource(
                    enterprise.enterpriseId(),
                    enterprise.name(),
                    enterprise.logoUrl()
            );
        }

        // 3. Construir el DTO de recurso principal con todos los datos.
        return new TraceabilityEventResource(
                entity.getId(),
                entity.getBatchId(),
                entity.getEventType(),
                entity.getEventDate(),
                actorName != null ? actorName : "Usuario Desconocido",
                enterpriseInfo,
                locationResource,
                entity.getBlockchainStatus().name(),
                entity.getTransactionHash(),
                entity.getProofImageUrl(),
                entity.getProofImageHash(),
                verificationUrl
        );
    }
}