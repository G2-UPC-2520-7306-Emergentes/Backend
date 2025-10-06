// application/outbound/iam/IamService.java
package com.foodchain.batch_management_context.application.outbound.iam;

import java.util.Optional;

public interface IamService {
    /**
     * Valida un token JWT y extrae los detalles del usuario.
     * @param token El token JWT a validar.
     * @return Un Optional con los UserDetails si el token es válido, o un Optional vacío si no lo es.
     */
    Optional<UserDetails> validateTokenAndGetUserDetails(String token);
}