// interfaces/rest/BatchController.java
package com.foodchain.batch_management_context.interfaces.rest;

import com.foodchain.batch_management_context.domain.model.commands.*;
import com.foodchain.batch_management_context.interfaces.rest.resources.EditBatchResource;
import com.foodchain.batch_management_context.domain.model.valueobjects.BatchId;
import com.foodchain.batch_management_context.domain.services.BatchCommandService;
import com.foodchain.batch_management_context.domain.services.BatchQueryService;
import com.foodchain.batch_management_context.interfaces.rest.resources.BatchResource;
import com.foodchain.batch_management_context.interfaces.rest.resources.CreateBatchResource;
import com.foodchain.batch_management_context.interfaces.rest.transform.BatchResourceFromEntityAssembler;
import com.foodchain.batch_management_context.interfaces.rest.transform.CreateBatchCommandFromResourceAssembler;
import com.foodchain.shared_domain.domain.model.aggregates.UserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/batches")
public class BatchController {

    private final BatchCommandService batchCommandService;
    private final BatchQueryService batchQueryService;

    public BatchController(BatchCommandService batchCommandService, BatchQueryService batchQueryService) {
        this.batchCommandService = batchCommandService;
        this.batchQueryService = batchQueryService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ENTERPRISE_USER') or hasRole('ENTERPRISE_ADMIN')")
    public ResponseEntity<BatchId> createBatch(@Valid @RequestBody CreateBatchResource resource,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        var command = CreateBatchCommandFromResourceAssembler.toCommandFromResource(resource, userDetails.enterpriseId());
        var batchId = batchCommandService.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(batchId);
    }

    @GetMapping
    @PreAuthorize("hasRole('ENTERPRISE_USER') or hasRole('ENTERPRISE_ADMIN')")
    public ResponseEntity<List<BatchResource>> getMyBatches(@AuthenticationPrincipal UserDetails userDetails) {
        var batches = batchQueryService.handle(userDetails.enterpriseId());
        var batchResources = batches.stream()
                .map(BatchResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(batchResources);
    }

    @PutMapping("/{batchId}")
    @PreAuthorize("hasRole('ENTERPRISE_USER') or hasRole('ENTERPRISE_ADMIN')")
    public ResponseEntity<Void> editBatch(@PathVariable UUID batchId,
                                          @Valid @RequestBody EditBatchResource resource,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        var command = new EditBatchCommand(batchId, userDetails.enterpriseId(), resource.productDescription());
        batchCommandService.handle(command);
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint para duplicar un lote existente.
     * Crea un nuevo lote con una nueva identidad pero datos copiados.
     */
    @PostMapping("/{originalBatchId}/duplicate")
    @PreAuthorize("hasRole('ENTERPRISE_USER') or hasRole('ENTERPRISE_ADMIN')")
    public ResponseEntity<BatchId> duplicateBatch(@PathVariable UUID originalBatchId,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        var command = new DuplicateBatchCommand(originalBatchId, userDetails.enterpriseId());
        var newBatchId = batchCommandService.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(newBatchId);
    }

    /**
     * Endpoint para eliminar un lote, si y solo si no tiene historial.
     */
    @DeleteMapping("/{batchId}")
    @PreAuthorize("hasRole('ENTERPRISE_USER') or hasRole('ENTERPRISE_ADMIN')")
    public ResponseEntity<Void> deleteBatch(@PathVariable UUID batchId,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        var command = new DeleteBatchCommand(batchId, userDetails.enterpriseId());
        batchCommandService.handle(command);
        // El código 204 No Content es el estándar para un DELETE exitoso.
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint para subir y asignar una imagen a un lote.
     * Acepta multipart/form-data.
     */
    @PostMapping("/{batchId}/image")
    @PreAuthorize("hasRole('ENTERPRISE_USER') or hasRole('ENTERPRISE_ADMIN')")
    public ResponseEntity<Void> uploadBatchImage(@PathVariable UUID batchId,
                                                 @RequestParam("file") MultipartFile file,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var command = new AssignImageToBatchCommand(batchId, userDetails.enterpriseId(), file);
        batchCommandService.handle(command);
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint para marcar un lote como "CERRADO".
     * Una vez cerrado, no se podrán añadir nuevos pasos ni editarlo.
     */
    @PutMapping("/{batchId}/close")
    @PreAuthorize("hasRole('ENTERPRISE_USER') or hasRole('ENTERPRISE_ADMIN')")
    public ResponseEntity<Void> closeBatch(@PathVariable UUID batchId,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        var command = new CloseBatchCommand(batchId, userDetails.enterpriseId());
        batchCommandService.handle(command);
        return ResponseEntity.ok().build();
    }
}