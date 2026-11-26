package com.logitracksa.project.controller;

import com.logitracksa.project.model.dto.request.MovimientoRequest;
import com.logitracksa.project.model.dto.response.MovimientoResponse;
import com.logitracksa.project.model.dto.response.PageResponse;
import com.logitracksa.project.service.MovimientoService;
import com.logitracksa.project.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Registro y consulta de movimientos de inventario.
 * Incluye paginación, filtro por fecha e historial por producto.
 */
@RestController
@RequestMapping("/movimientos")
@Tag(name = "Movimientos", description = "Entradas, salidas y transferencias de inventario")
public class MovimientoController {

    private final MovimientoService movimientoService;

    public MovimientoController(MovimientoService movimientoService) {
        this.movimientoService = movimientoService;
    }

    @GetMapping
    @Operation(summary = "Listar todos los movimientos")
    public ResponseEntity<List<MovimientoResponse>> findAll() {
        return ResponseEntity.ok(movimientoService.findAll());
    }

    @GetMapping("/paginado")
    @Operation(summary = "Movimientos con paginación",
               description = "Parámetros: page (0-based), size, sort (fecha|tipo), dir (ASC|DESC)")
    public ResponseEntity<PageResponse<MovimientoResponse>> findAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha") String sort,
            @RequestParam(defaultValue = "DESC") String dir) {
        return ResponseEntity.ok(movimientoService.findAllPaged(page, size, sort, dir));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener movimiento por id")
    public ResponseEntity<MovimientoResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(movimientoService.findById(id));
    }

    @GetMapping("/producto/{productoId}/historial")
    @Operation(summary = "Historial de movimientos de un producto (trazabilidad)",
               description = "Devuelve todos los movimientos en que participó el producto, del más reciente al más antiguo.")
    public ResponseEntity<List<MovimientoResponse>> historialPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(movimientoService.historialPorProducto(productoId));
    }

    @PostMapping
    @Operation(summary = "Registrar movimiento",
               description = "Registra una ENTRADA, SALIDA o TRANSFERENCIA. "
                           + "Las entradas validan la capacidad de la bodega destino.")
    public ResponseEntity<MovimientoResponse> registrar(@Valid @RequestBody MovimientoRequest request) {
        return ResponseEntity.ok(movimientoService.registrar(request, SecurityUtils.getCurrentUsername()));
    }

    @GetMapping("/fecha")
    @Operation(summary = "Movimientos por rango de fechas",
               description = "Formato ISO 8601: 2026-11-01T00:00:00")
    public ResponseEntity<List<MovimientoResponse>> findByFechaBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(movimientoService.findByFechaBetween(inicio, fin));
    }
}
