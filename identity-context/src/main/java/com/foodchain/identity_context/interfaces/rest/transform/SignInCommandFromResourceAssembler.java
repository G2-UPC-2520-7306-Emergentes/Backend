// interfaces/rest/transform/SignInCommandFromResourceAssembler.java
package com.foodchain.identity_context.interfaces.rest.transform;

import com.foodchain.identity_context.domain.model.commands.SignInCommand;
import com.foodchain.identity_context.interfaces.rest.resources.SignInResource;

public class SignInCommandFromResourceAssembler {
    public static SignInCommand toCommandFromResource(SignInResource resource) {
        return new SignInCommand(resource.email(), resource.password());
    }
}