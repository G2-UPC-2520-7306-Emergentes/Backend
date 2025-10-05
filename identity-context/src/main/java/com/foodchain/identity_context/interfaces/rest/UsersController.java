// interfaces/rest/UsersController.java
package com.foodchain.identity_context.interfaces.rest;

import com.foodchain.identity_context.domain.services.UserQueryService;
import com.foodchain.identity_context.interfaces.rest.resources.UserProfileResource;
import com.foodchain.identity_context.interfaces.rest.transform.UserProfileResourceFromEntityAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/iam/users")
public class UsersController {
    // En un futuro, aquí irían endpoints para listar usuarios, etc.
}