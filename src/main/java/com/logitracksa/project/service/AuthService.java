package com.logitracksa.project.service;

import com.logitracksa.project.exception.BusinessException;
import com.logitracksa.project.model.dto.auth.JwtResponse;
import com.logitracksa.project.model.dto.auth.LoginRequest;
import com.logitracksa.project.model.dto.auth.RegisterRequest;
import com.logitracksa.project.model.entity.Usuario;
import com.logitracksa.project.repository.UsuarioRepository;
import com.logitracksa.project.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Autenticación: login (genera JWT) y registro de nuevos usuarios.
 */
@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider,
                       AuthenticationManager authenticationManager) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Valida credenciales y devuelve un token JWT.
     */
    public JwtResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        Usuario usuario = usuarioRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        String token = jwtTokenProvider.generateToken(usuario.getUsername(), usuario.getRol().name());
        return new JwtResponse(token, "Bearer", usuario.getUsername(),
                usuario.getRol(), jwtTokenProvider.getExpiration());
    }

    /**
     * Registra un nuevo usuario y devuelve un token JWT.
     */
    @Transactional
    public JwtResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("El username ya está en uso");
        }
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("El email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setEmail(request.getEmail());
        usuario.setNombre(request.getNombre());
        usuario.setRol(request.getRol());
        usuario.setEnabled(true);
        usuarioRepository.save(usuario);

        String token = jwtTokenProvider.generateToken(usuario.getUsername(), usuario.getRol().name());
        return new JwtResponse(token, "Bearer", usuario.getUsername(),
                usuario.getRol(), jwtTokenProvider.getExpiration());
    }
}
