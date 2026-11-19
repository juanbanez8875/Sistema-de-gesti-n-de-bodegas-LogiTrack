package com.logitracksa.project.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.logitracksa.project.listener.AuditEntityListener;
import com.logitracksa.project.model.enums.TipoMovimiento;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Movimiento de inventario (entrada, salida o transferencia).
 */
@Entity
@Table(name = "movimiento")
@EntityListeners(AuditEntityListener.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@NoArgsConstructor
public class Movimiento extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoMovimiento tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    private Usuario usuario;

    /** Bodega de origen (obligatoria en SALIDA y TRANSFERENCIA). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bodega_origen_id")
    private Bodega bodegaOrigen;

    /** Bodega de destino (obligatoria en ENTRADA y TRANSFERENCIA). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bodega_destino_id")
    private Bodega bodegaDestino;

    @OneToMany(mappedBy = "movimiento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("id ASC")
    private List<DetalleMovimiento> detalles = new ArrayList<>();

    // ----- Helpers de auditoría (no disparan lazy-loading) -----

    @Transient
    public Long getBodegaOrigenId() {
        return bodegaOrigen != null ? bodegaOrigen.getId() : null;
    }

    @Transient
    public Long getBodegaDestinoId() {
        return bodegaDestino != null ? bodegaDestino.getId() : null;
    }

    @Transient
    public String getUsuarioUsername() {
        return usuario != null ? usuario.getUsername() : null;
    }

    public void addDetalle(DetalleMovimiento detalle) {
        detalles.add(detalle);
        detalle.setMovimiento(this);
    }
}
