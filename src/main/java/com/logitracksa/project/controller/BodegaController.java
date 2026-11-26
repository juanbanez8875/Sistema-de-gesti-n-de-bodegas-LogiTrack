package com.logitracksa.project.controller;

import com.logitracksa.project.model.dto.request.BodegaRequest;
import com.logitracksa.project.model.dto.response.BodegaResponse;
import com.logitracksa.project.model.dto.response.PageResponse;
import com.logitracksa.project.service.BodegaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CRUD de bodegas con búsqueda y paginación.
 * Las operaciones de escritura requieren rol ADMIN.
 * Al actualizar, valida que la capacidad no sea inferior al stock actual.
 */
@RestController
@RequestMapping("/bodegas")
@Tag(name = "Bodegas", description = "Gestión de bodegas")
public class BodegaController {

    private final BodegaService bodegaService;

    public BodegaController(BodegaService bodegaService) {
        this.bodegaService = bodegaService;
    }

    @GetMapping
    @Operation(summary = "Listar todas las bodegas")
    public ResponseEntity<List<BodegaResponse>> findAll() {
        return ResponseEntity.ok(bodegaService.findAll());
    }

    @GetMapping("/paginado")
    @Operation(summary = "Bodegas con paginación",
               description = "Parámetros: page (0-based), size, sort (nombre|ubicacion|capacidad), dir (ASC|DESC)")
    public ResponseEntity<PageResponse<BodegaResponse>> findAllPaged(
            @Parameter(description = "Número de página (inicia en 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenamiento") @RequestParam(defaultValue = "nombre") String sort,
            @Parameter(description = "Dirección: ASC o DESC") @RequestParam(defaultValue = "ASC") String dir) {
        return ResponseEntity.ok(bodegaService.findAllPaged(page, size, sort, dir));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Búsqueda por nombre o ubicación (LIKE, insensible a mayúsculas)")
    public ResponseEntity<List<BodegaResponse>> buscar(
            @Parameter(description = "Texto a buscar") @RequestParam String q) {
        return ResponseEntity.ok(bodegaService.buscar(q));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener bodega por id")
    public ResponseEntity<BodegaResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(bodegaService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear bodega (solo ADMIN)")
    public ResponseEntity<BodegaResponse> create(@Valid @RequestBody BodegaRequest request) {
        return ResponseEntity.ok(bodegaService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar bodega (solo ADMIN)",
               description = "Si la nueva capacidad es menor al stock actual, devuelve 400.")
    public ResponseEntity<BodegaResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody BodegaRequest request) {
        return ResponseEntity.ok(bodegaService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar bodega (solo ADMIN)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bodegaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
