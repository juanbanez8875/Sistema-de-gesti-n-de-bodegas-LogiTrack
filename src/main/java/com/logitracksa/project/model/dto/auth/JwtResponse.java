package com.logitracksa.project.model.dto.auth;

import com.logitracksa.project.model.enums.Rol;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class JwtResponse {

    private String token;
    private String type;
    private String username;
    private Rol rol;
    private long expiresIn;
}
