package com.logitracksa.project.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BodegaResponse {

    private Long id;
    private String nombre;
    private String ubicacion;
    private Integer capacidad;
    private String encargado;
}
