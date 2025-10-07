// interfaces/rest/BatchController.java
package com.foodchain.batch_management_context.interfaces.rest;

import com.foodchain.batch_management_context.domain.model.valueobjects.BatchId;
import com.foodchain.batch_management_context.domain.services.BatchCommandService;
import com.foodchain.batch_management_context.interfaces.rest.resources.CreateBatchResource;
import com.foodchain.batch_management_context.interfaces.rest.transform.CreateBatchCommandFromResourceAssembler;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/batches")
public class BatchController {

    private final BatchCommandService batchCommandService;

    public BatchController(BatchCommandService batchCommandService) {
        this.batchCommandService = batchCommandService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ENTERPRISE_USER') or hasRole('ENTERPRISE_ADMIN')") // ¡ENDPOINT PROTEGIDO!
    public ResponseEntity<BatchId> createBatch(@Valid @RequestBody CreateBatchResource resource) {
        var command = CreateBatchCommandFromResourceAssembler.toCommandFromResource(resource);
        var batchId = batchCommandService.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(batchId);
    }
}