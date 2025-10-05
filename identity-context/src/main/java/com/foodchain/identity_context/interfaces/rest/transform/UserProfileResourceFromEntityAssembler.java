// interfaces/rest/transform/UserProfileResourceFromEntityAssembler.java
package com.foodchain.identity_context.interfaces.rest.transform;

import com.foodchain.identity_context.domain.model.aggregates.User;
import com.foodchain.identity_context.interfaces.rest.resources.UserProfileResource;

public class UserProfileResourceFromEntityAssembler {
    public static UserProfileResource toResourceFromEntity(User user) {
        return new UserProfileResource(user.getId(), user.getEmail(), user.getEnterpriseId());
    }
}