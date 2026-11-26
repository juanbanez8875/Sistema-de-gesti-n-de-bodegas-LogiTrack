package com.logitracksa.project.controller;

import com.logitracksa.project.model.dto.response.EstadisticasResponse;
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
 * Incluye el resumen general, stock bajo, estadísticas agregadas y movimientos por fecha.
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
    @Operation(summary = "Resumen general",
               description = "Stock total por bodega, productos más movidos y totales del sistema.")
    public ResponseEntity<ReporteResponse> resumenGeneral() {
        return ResponseEntity.ok(reporteService.resumenGeneral());
    }

    @GetMapping("/estadisticas")
    @Operation(summary = "Estadísticas agregadas (GROUP BY)",
               description = "Movimientos por tipo, movimientos por mes, productos por categoría, "
                           + "valor monetario por bodega y valor total del inventario.")
    public ResponseEntity<EstadisticasResponse> estadisticas() {
        return ResponseEntity.ok(reporteService.estadisticas());
    }

    @GetMapping("/stock-bajo")
    @Operation(summary = "Productos con stock bajo (menos de 10 unidades)")
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
