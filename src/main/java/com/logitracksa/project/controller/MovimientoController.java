package com.logitracksa.project.controller;

import com.logitracksa.project.model.dto.request.MovimientoRequest;
import com.logitracksa.project.model.dto.response.MovimientoResponse;
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

    @GetMapping("/{id}")
    @Operation(summary = "Obtener movimiento por id")
    public ResponseEntity<MovimientoResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(movimientoService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Registrar movimiento", description = "Registra una entrada, salida o transferencia")
    public ResponseEntity<MovimientoResponse> registrar(@Valid @RequestBody MovimientoRequest request) {
        String username = SecurityUtils.getCurrentUsername();
        return ResponseEntity.ok(movimientoService.registrar(request, username));
    }

    @GetMapping("/fecha")
    @Operation(summary = "Movimientos por rango de fechas", description = "Parámetros: inicio y fin (formato ISO 8601)")
    public ResponseEntity<List<MovimientoResponse>> findByFechaBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(movimientoService.findByFechaBetween(inicio, fin));
    }
}
