// interfaces/rest/BatchController.java
package com.foodchain.batch_management_context.interfaces.rest;

import com.foodchain.batch_management_context.domain.model.commands.*;
import com.foodchain.batch_management_context.domain.model.queries.GetBatchByIdQuery;
import com.foodchain.batch_management_context.interfaces.rest.resources.BatchOwnerResource;
import com.foodchain.batch_management_context.interfaces.rest.resources.EditBatchResource;
import com.foodchain.batch_management_context.domain.model.valueobjects.BatchId;
import com.foodchain.batch_management_context.domain.services.BatchCommandService;
import com.foodchain.batch_management_context.domain.services.BatchQueryService;
import com.foodchain.batch_management_context.interfaces.rest.resources.BatchResource;
import com.foodchain.batch_management_context.interfaces.rest.resources.CreateBatchResource;
import com.foodchain.batch_management_context.interfaces.rest.transform.BatchResourceFromEntityAssembler;
import com.foodchain.batch_management_context.interfaces.rest.transform.CreateBatchCommandFromResourceAssembler;
import com.foodchain.shared_domain.domain.model.aggregates.UserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
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
@Tag(name = "Batch Management", description = "API para la gestión del ciclo de vida de los lotes de producción.")
public class BatchController {

    private final BatchCommandService batchCommandService;
    private final BatchQueryService batchQueryService;

    public BatchController(BatchCommandService batchCommandService, BatchQueryService batchQueryService) {
        this.batchCommandService = batchCommandService;
        this.batchQueryService = batchQueryService;
    }

    @Operation(summary = "Crear un nuevo lote", description = "Crea un nuevo lote asociado a la empresa del usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Lote creado exitosamente", content = @Content(schema = @Schema(implementation = BatchId.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "403", description = "No autorizado para realizar esta acción")
    })
    @PostMapping
    @PreAuthorize("hasRole('ENTERPRISE_USER') or hasRole('ENTERPRISE_ADMIN')")
    public ResponseEntity<BatchId> createBatch(@Valid @RequestBody CreateBatchResource resource,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        var command = CreateBatchCommandFromResourceAssembler.toCommandFromResource(resource, userDetails.enterpriseId());
        var batchId = batchCommandService.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(batchId);
    }

    @Operation(summary = "Obtener mis lotes", description = "Devuelve una lista de todos los lotes pertenecientes a la empresa del usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de lotes recuperada exitosamente"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping
    @PreAuthorize("hasRole('ENTERPRISE_USER') or hasRole('ENTERPRISE_ADMIN')")
    public ResponseEntity<List<BatchResource>> getMyBatches(@AuthenticationPrincipal UserDetails userDetails) {
        var batches = batchQueryService.handle(userDetails.enterpriseId());
        var batchResources = batches.stream()
                .map(BatchResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(batchResources);
    }

    @Operation(summary = "Editar un lote", description = "Modifica la descripción de un lote existente. Solo se puede editar un lote si su estado no es 'CERRADO'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lote actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Lote no encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflicto: El lote ya está cerrado y no se puede editar")
    })
    @PutMapping("/{batchId}")
    @PreAuthorize("hasRole('ENTERPRISE_USER') or hasRole('ENTERPRISE_ADMIN')")
    public ResponseEntity<Void> editBatch(
            @Parameter(description = "ID del lote a editar") @PathVariable UUID batchId,
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

    /**
     * Endpoint interno para que otros servicios verifiquen la propiedad de un lote.
     * Es crucial para la autorización entre microservicios.
     */
    @GetMapping("/{batchId}/owner")
    @PreAuthorize("isAuthenticated()") // Debe ser llamado por otro servicio autenticado
    public ResponseEntity<BatchOwnerResource> getBatchOwner(
            @Parameter(description = "ID del lote a consultar") @PathVariable UUID batchId) {

        var uuid = new BatchId(batchId);
        var query = new GetBatchByIdQuery(uuid);
        var batch = batchQueryService.handle(query)
                .orElseThrow(() -> new EntityNotFoundException("Lote no encontrado"));

        return ResponseEntity.ok(new BatchOwnerResource(batch.getEnterpriseId()));
    }
}