// interfaces/rest/transform/UserResourceFromEntityAssembler.java
package com.foodchain.identity_context.interfaces.rest.transform;

import com.foodchain.identity_context.domain.model.aggregates.User;
import com.foodchain.identity_context.interfaces.rest.resources.UserResource;

public class UserResourceFromEntityAssembler {
    public static UserResource toResourceFromEntity(User entity) {
        return new UserResource(
                entity.getId(),
                entity.getEmail(),
                entity.getEnterpriseId(),
                entity.getRoleStrings() // Usamos el método conveniente que ya teníamos en el Agregado
        );
    }
}