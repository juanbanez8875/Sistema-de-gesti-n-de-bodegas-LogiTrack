package com.logitracksa.project.model.dto.response;

import com.logitracksa.project.model.enums.TipoMovimiento;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MovimientoResponse {

    private Long id;
    private LocalDateTime fecha;
    private TipoMovimiento tipo;
    private String usuarioUsername;
    private String bodegaOrigenNombre;
    private String bodegaDestinoNombre;
    private List<DetalleMovimientoResponse> detalles;
}
