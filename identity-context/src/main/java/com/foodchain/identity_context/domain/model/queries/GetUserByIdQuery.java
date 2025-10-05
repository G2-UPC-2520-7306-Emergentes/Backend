// domain/model/queries/GetUserByIdQuery.java
package com.foodchain.identity_context.domain.model.queries;

import java.util.UUID;

/**
 * Consulta para obtener los detalles de un usuario por su ID.
 * @param userId El ID del usuario a consultar.
 */
public record GetUserByIdQuery(UUID userId) {
}