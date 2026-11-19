package com.logitracksa.project.controller;

import com.logitracksa.project.model.dto.response.MovimientoResponse;
import com.logitracksa.project.model.dto.response.ProductoResponse;
import com.logitracksa.project.model.dto.response.ReporteResponse;
import com.logitracksa.project.service.MovimientoService;
import com.logitracksa.project.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Reportes y consultas avanzadas.
 */
@RestController
@RequestMapping("/reportes")
@Tag(name = "Reportes", description = "Consultas avanzadas y reportes de inventario")
public class ReporteController {

    private final ReporteService reporteService;
    private final MovimientoService movimientoService;

    public ReporteController(ReporteService reporteService, MovimientoService movimientoService) {
        this.reporteService = reporteService;
        this.movimientoService = movimientoService;
    }

    @GetMapping("/resumen")
    @Operation(summary = "Resumen general", description = "Stock total por bodega y productos más movidos")
    public ResponseEntity<ReporteResponse> resumenGeneral() {
        return ResponseEntity.ok(reporteService.resumenGeneral());
    }

    @GetMapping("/stock-bajo")
    @Operation(summary = "Productos con stock bajo", description = "Productos con menos de 10 unidades")
    public ResponseEntity<List<ProductoResponse>> stockBajo() {
        return ResponseEntity.ok(reporteService.stockBajo());
    }

    @GetMapping("/movimientos")
    @Operation(summary = "Movimientos por rango de fechas")
    public ResponseEntity<List<MovimientoResponse>> movimientosPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(movimientoService.findByFechaBetween(inicio, fin));
    }
}
