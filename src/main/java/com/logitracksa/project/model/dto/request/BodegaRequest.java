package com.logitracksa.project.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BodegaRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String nombre;

    @Size(max = 200, message = "La ubicación no puede superar 200 caracteres")
    private String ubicacion;

    @Min(value = 0, message = "La capacidad no puede ser negativa")
    private Integer capacidad;

    @Size(max = 100, message = "El encargado no puede superar 100 caracteres")
    private String encargado;
}
