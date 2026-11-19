package com.logitracksa.project.model.dto.response;

import com.logitracksa.project.model.enums.TipoOperacion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class AuditoriaResponse {

    private Long id;
    private TipoOperacion tipoOperacion;
    private LocalDateTime fechaHora;
    private String usuario;
    private String entidad;
    private Long entidadId;
    private String valoresAnteriores;
    private String valoresNuevos;
}
