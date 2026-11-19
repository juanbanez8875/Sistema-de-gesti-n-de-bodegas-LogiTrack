package com.logitracksa.project.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DetalleMovimientoResponse {

    private Long productoId;
    private String productoNombre;
    private Integer cantidad;
}
