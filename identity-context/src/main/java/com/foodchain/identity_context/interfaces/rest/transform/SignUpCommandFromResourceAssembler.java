// interfaces/rest/transform/SignUpCommandFromResourceAssembler.java
package com.foodchain.identity_context.interfaces.rest.transform;


import com.foodchain.identity_context.domain.model.commands.SignUpCommand;
import com.foodchain.identity_context.interfaces.rest.resources.SignUpResource;

public class SignUpCommandFromResourceAssembler {
    public static SignUpCommand toCommandFromResource(SignUpResource resource) {
        return new SignUpCommand(resource.enterpriseId(), resource.email(), resource.password());
    }
}