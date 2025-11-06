// EN: traceability-context/application/outbound/iam/UserQueryService.java
package com.foodchain.traceability_context.application.outbound.iam;

import com.foodchain.shared_domain.domain.model.aggregates.UserDetails;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface UserQueryService {
    /**
     * Dada una lista de IDs de usuario, devuelve un mapa que asocia cada ID
     * con el objeto UserDetails completo correspondiente.
     *
     * @param userIds La lista de UUIDs de los usuarios a consultar.
     * @return Un mapa donde la clave es el UUID del usuario y el valor es el objeto UserDetails.
     */
    Map<UUID, UserDetails> getUserDetailsForIds(List<UUID> userIds);
}