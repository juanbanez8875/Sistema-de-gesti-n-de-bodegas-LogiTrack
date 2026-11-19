package com.logitracksa.project.controller;

import com.logitracksa.project.model.dto.auth.JwtResponse;
import com.logitracksa.project.model.dto.auth.LoginRequest;
import com.logitracksa.project.model.dto.auth.RegisterRequest;
import com.logitracksa.project.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints públicos de autenticación.
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación", description = "Login y registro de usuarios con JWT")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Valida credenciales y devuelve un token JWT")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuario", description = "Crea un nuevo usuario y devuelve un token JWT")
    public ResponseEntity<JwtResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
}
