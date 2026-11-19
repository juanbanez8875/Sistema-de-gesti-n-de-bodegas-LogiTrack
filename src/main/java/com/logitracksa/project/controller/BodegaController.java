package com.logitracksa.project.controller;

import com.logitracksa.project.model.dto.request.BodegaRequest;
import com.logitracksa.project.model.dto.response.BodegaResponse;
import com.logitracksa.project.service.BodegaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CRUD de bodegas. Las operaciones de escritura requieren rol ADMIN.
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
    @Operation(summary = "Actualizar bodega (solo ADMIN)")
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
