package com.logitracksa.project.controller;

import com.logitracksa.project.model.dto.response.AuditoriaResponse;
import com.logitracksa.project.model.enums.TipoOperacion;
import com.logitracksa.project.service.AuditoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Consultas de auditoría. Acceso restringido a ADMIN.
 */
@RestController
@RequestMapping("/auditorias")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Auditoría", description = "Registros de cambios en entidades (solo ADMIN)")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    @Operation(summary = "Listar todas las auditorías")
    public ResponseEntity<List<AuditoriaResponse>> findAll() {
        return ResponseEntity.ok(auditoriaService.findAll());
    }

    @GetMapping("/usuario/{usuario}")
    @Operation(summary = "Auditorías por usuario")
    public ResponseEntity<List<AuditoriaResponse>> findByUsuario(@PathVariable String usuario) {
        return ResponseEntity.ok(auditoriaService.findByUsuario(usuario));
    }

    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Auditorías por tipo de operación (INSERT, UPDATE, DELETE)")
    public ResponseEntity<List<AuditoriaResponse>> findByTipo(@PathVariable TipoOperacion tipo) {
        return ResponseEntity.ok(auditoriaService.findByTipoOperacion(tipo));
    }

    @GetMapping("/entidad/{entidad}")
    @Operation(summary = "Auditorías por entidad afectada")
    public ResponseEntity<List<AuditoriaResponse>> findByEntidad(@PathVariable String entidad) {
        return ResponseEntity.ok(auditoriaService.findByEntidad(entidad));
    }

    @GetMapping("/fecha")
    @Operation(summary = "Auditorías por rango de fechas")
    public ResponseEntity<List<AuditoriaResponse>> findByFechaBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(auditoriaService.findByFechaBetween(inicio, fin));
    }
}
