package com.logitracksa.project.controller;

import com.logitracksa.project.model.dto.request.ProductoRequest;
import com.logitracksa.project.model.dto.response.PageResponse;
import com.logitracksa.project.model.dto.response.ProductoResponse;
import com.logitracksa.project.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CRUD de productos con búsqueda y paginación.
 * Las operaciones de escritura requieren rol ADMIN.
 */
@RestController
@RequestMapping("/productos")
@Tag(name = "Productos", description = "Gestión de productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    @Operation(summary = "Listar todos los productos")
    public ResponseEntity<List<ProductoResponse>> findAll() {
        return ResponseEntity.ok(productoService.findAll());
    }

    @GetMapping("/paginado")
    @Operation(summary = "Productos con paginación",
               description = "Parámetros: page (0-based), size, sort (nombre|categoria|stock|precio), dir (ASC|DESC)")
    public ResponseEntity<PageResponse<ProductoResponse>> findAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombre") String sort,
            @RequestParam(defaultValue = "ASC") String dir) {
        return ResponseEntity.ok(productoService.findAllPaged(page, size, sort, dir));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Búsqueda por nombre (LIKE, insensible a mayúsculas)")
    public ResponseEntity<List<ProductoResponse>> buscarPorNombre(
            @Parameter(description = "Texto a buscar en el nombre") @RequestParam String nombre) {
        return ResponseEntity.ok(productoService.buscarPorNombre(nombre));
    }

    @GetMapping("/categoria")
    @Operation(summary = "Filtrar por categoría (insensible a mayúsculas)")
    public ResponseEntity<List<ProductoResponse>> buscarPorCategoria(
            @Parameter(description = "Nombre exacto de la categoría") @RequestParam String categoria) {
        return ResponseEntity.ok(productoService.buscarPorCategoria(categoria));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por id")
    public ResponseEntity<ProductoResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear producto (solo ADMIN)")
    public ResponseEntity<ProductoResponse> create(@Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.ok(productoService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar producto (solo ADMIN)")
    public ResponseEntity<ProductoResponse> update(@PathVariable Long id,
                                                     @Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.ok(productoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar producto (solo ADMIN)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
