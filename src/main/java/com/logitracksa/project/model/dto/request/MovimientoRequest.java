package com.logitracksa.project.model.dto.request;

import com.logitracksa.project.model.enums.TipoMovimiento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MovimientoRequest {

    @NotNull(message = "El tipo de movimiento es obligatorio")
    private TipoMovimiento tipo;

    /** Requerido en SALIDA y TRANSFERENCIA. */
    private Long bodegaOrigenId;

    /** Requerido en ENTRADA y TRANSFERENCIA. */
    private Long bodegaDestinoId;

    @NotEmpty(message = "El movimiento debe tener al menos un detalle")
    @Valid
    private List<DetalleMovimientoRequest> detalles;
}
