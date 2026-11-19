package com.logitracksa.project.model.entity;

import com.logitracksa.project.model.enums.TipoOperacion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Registro de auditoría. No se audita a sí misma (sin @EntityListeners).
 */
@Entity
@Table(name = "auditoria")
@Getter
@Setter
@NoArgsConstructor
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoOperacion tipoOperacion;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Column(length = 100)
    private String usuario;

    @Column(nullable = false, length = 100)
    private String entidad;

    @Column
    private Long entidadId;

    @Column(name = "valores_anteriores", columnDefinition = "TEXT")
    private String valoresAnteriores;

    @Column(name = "valores_nuevos", columnDefinition = "TEXT")
    private String valoresNuevos;
}
