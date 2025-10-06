// identity-context/interfaces/rest/transform/UserDetailsResourceFromEntityAssembler.java
package com.foodchain.identity_context.interfaces.rest.transform;

import com.foodchain.identity_context.domain.model.aggregates.User;
import com.foodchain.identity_context.interfaces.rest.resources.UserDetailsResource;

public class UserDetailsResourceFromEntityAssembler {
    public static UserDetailsResource toResourceFromEntity(User user) {
        return new UserDetailsResource(user.getId(), user.getEmail(), user.getEnterpriseId(), user.getRoleStrings());
    }
}