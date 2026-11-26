package com.logitracksa.project.controller;

import com.logitracksa.project.model.dto.auth.JwtResponse;
import com.logitracksa.project.model.dto.auth.LoginRequest;
import com.logitracksa.project.model.dto.auth.RegisterRequest;
import com.logitracksa.project.model.dto.request.CambioPasswordRequest;
import com.logitracksa.project.model.dto.response.PerfilResponse;
import com.logitracksa.project.service.AuthService;
import com.logitracksa.project.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints de autenticación: login, registro, perfil y cambio de contraseña.
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación", description = "Login, registro, perfil y cambio de contraseña")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión — devuelve un token JWT")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuario — devuelve un token JWT")
    public ResponseEntity<JwtResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @GetMapping("/perfil")
    @Operation(summary = "Ver mi perfil (usuario autenticado)")
    public ResponseEntity<PerfilResponse> perfil() {
        return ResponseEntity.ok(authService.perfil(SecurityUtils.getCurrentUsername()));
    }

    @PutMapping("/password")
    @Operation(summary = "Cambiar mi contraseña (usuario autenticado)",
               description = "Requiere la contraseña actual y la nueva. Mínimo 4 caracteres.")
    public ResponseEntity<Void> cambiarPassword(@Valid @RequestBody CambioPasswordRequest request) {
        authService.cambiarPassword(SecurityUtils.getCurrentUsername(), request);
        return ResponseEntity.noContent().build();
    }
}
