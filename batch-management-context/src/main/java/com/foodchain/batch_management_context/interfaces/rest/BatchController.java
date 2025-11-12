// interfaces/rest/BatchController.java
package com.foodchain.batch_management_context.interfaces.rest;

import com.foodchain.batch_management_context.domain.model.aggregates.Batch;
import com.foodchain.batch_management_context.domain.model.commands.*;
import com.foodchain.batch_management_context.domain.model.queries.GetBatchByIdQuery;
import com.foodchain.batch_management_context.domain.model.queries.GetBatchCountByEnterprise;
import com.foodchain.batch_management_context.interfaces.rest.resources.*;
import com.foodchain.batch_management_context.domain.model.valueobjects.BatchId;
import com.foodchain.batch_management_context.domain.services.BatchCommandService;
import com.foodchain.batch_management_context.domain.services.BatchQueryService;
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
        CreateBatchCommand command = CreateBatchCommandFromResourceAssembler.toCommandFromResource(resource, userDetails.enterpriseId());
        BatchId batchId = batchCommandService.handle(command);
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
        List<Batch> batches = batchQueryService.handle(userDetails.enterpriseId());
        List<BatchResource> batchResources = batches.stream()
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
        EditBatchCommand command = new EditBatchCommand(batchId, userDetails.enterpriseId(), resource.productDescription());
        batchCommandService.handle(command);
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint para duplicar un lote existente.
     * Crea un nuevo lote con una nueva identidad pero datos copiados.
     */
    @Operation(summary = "Duplicar un lote", description = "Crea una copia de un lote existente, asignándole una nueva identidad.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Lote duplicado exitosamente", content = @Content(schema = @Schema(implementation = BatchId.class))),
            @ApiResponse(responseCode = "404", description = "Lote original no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado para realizar esta acción")
    })
    @PostMapping("/{originalBatchId}/duplicate")
    @PreAuthorize("hasRole('ENTERPRISE_USER') or hasRole('ENTERPRISE_ADMIN')")
    public ResponseEntity<BatchId> duplicateBatch(@PathVariable UUID originalBatchId,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        DuplicateBatchCommand command = new DuplicateBatchCommand(originalBatchId, userDetails.enterpriseId());
        BatchId newBatchId = batchCommandService.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(newBatchId);
    }

    /**
     * Endpoint para eliminar un lote, si y solo si no tiene historial.
     */
    @Operation(summary = "Eliminar un lote", description = "Elimina un lote existente siempre que no tenga historial asociado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Lote eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Lote no encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflicto: El lote tiene historial y no se puede eliminar")
    })
    @DeleteMapping("/{batchId}")
    @PreAuthorize("hasRole('ENTERPRISE_USER') or hasRole('ENTERPRISE_ADMIN')")
    public ResponseEntity<Void> deleteBatch(@PathVariable UUID batchId,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        var command = new DeleteBatchCommand(batchId, userDetails.enterpriseId());
        batchCommandService.handle(command);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint para subir y asignar una imagen a un lote.
     * Acepta multipart/form-data.
     */
    @Operation(summary = "Subir imagen para un lote", description = "Asigna una imagen al lote especificado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Imagen subida y asignada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Archivo inválido o no proporcionado"),
            @ApiResponse(responseCode = "404", description = "Lote no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado para realizar esta acción")
    })
    @PostMapping("/{batchId}/image")
    @PreAuthorize("hasRole('ENTERPRISE_USER') or hasRole('ENTERPRISE_ADMIN')")
    public ResponseEntity<Void> uploadBatchImage(@PathVariable UUID batchId,
                                                 @RequestParam("file") MultipartFile file,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        AssignImageToBatchCommand command = new AssignImageToBatchCommand(batchId, userDetails.enterpriseId(), file);
        batchCommandService.handle(command);
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint para marcar un lote como "CERRADO".
     * Una vez cerrado, no se podrán añadir nuevos pasos ni editarlo.
     */
    @Operation(summary = "Cerrar un lote", description = "Marca un lote como 'CERRADO', impidiendo futuras modificaciones.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lote cerrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Lote no encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflicto: El lote ya está cerrado")
    })
    @PutMapping("/{batchId}/close")
    @PreAuthorize("hasRole('ENTERPRISE_USER') or hasRole('ENTERPRISE_ADMIN')")
    public ResponseEntity<Void> closeBatch(@PathVariable UUID batchId,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        CloseBatchCommand command = new CloseBatchCommand(batchId, userDetails.enterpriseId());
        batchCommandService.handle(command);
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint interno para que otros servicios verifiquen la propiedad de un lote.
     * Es crucial para la autorización entre microservicios.
     */
    @Operation(summary = "Obtener propietario del lote", description = "Devuelve el ID de la empresa propietaria del lote especificado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Propietario del lote recuperado exitosamente", content = @Content(schema = @Schema(implementation = BatchOwnerResource.class))),
            @ApiResponse(responseCode = "404", description = "Lote no encontrado")
    })
    @GetMapping("/{batchId}/owner")
    @PreAuthorize("isAuthenticated()") // Debe ser llamado por otro servicio autenticado
    public ResponseEntity<BatchOwnerResource> getBatchOwner(
            @Parameter(description = "ID del lote a consultar") @PathVariable UUID batchId) {

        BatchId uuid = new BatchId(batchId);
        GetBatchByIdQuery query = new GetBatchByIdQuery(uuid);
        Batch batch = batchQueryService.handle(query)
                .orElseThrow(() -> new EntityNotFoundException("Lote no encontrado"));

        return ResponseEntity.ok(new BatchOwnerResource(batch.getEnterpriseId()));
    }

    @Operation(summary = "Obtener conteo de lotes", description = "Devuelve el número total de lotes para la empresa del usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conteo de lotes recuperado exitosamente", content = @Content(schema = @Schema(implementation = BatchCountResource.class))),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping("/metrics/count")
    @PreAuthorize("hasRole('ENTERPRISE_ADMIN')")
    public ResponseEntity<BatchCountResource> countMyBatches(@AuthenticationPrincipal UserDetails userDetails) {
        GetBatchCountByEnterprise query = new GetBatchCountByEnterprise(userDetails.enterpriseId());
        long count = batchQueryService.handle(query);
        return ResponseEntity.ok(new BatchCountResource(count));
    }
}