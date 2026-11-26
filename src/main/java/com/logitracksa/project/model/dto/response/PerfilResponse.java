package com.logitracksa.project.model.dto.response;

import com.logitracksa.project.model.enums.Rol;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Datos del usuario autenticado (sin contraseña).
 */
@Getter
@Setter
@AllArgsConstructor
public class PerfilResponse {

    private Long id;
    private String username;
    private String email;
    private String nombre;
    private Rol rol;
    private Boolean enabled;
}
