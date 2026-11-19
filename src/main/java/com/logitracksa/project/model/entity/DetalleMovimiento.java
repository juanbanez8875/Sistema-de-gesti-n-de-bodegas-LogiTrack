package com.logitracksa.project.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Detalle de un movimiento: producto y cantidad afectada.
 */
@Entity
@Table(name = "detalle_movimiento")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@NoArgsConstructor
public class DetalleMovimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movimiento_id", nullable = false)
    @JsonIgnore
    private Movimiento movimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    // ----- Helpers de auditoría -----

    @Transient
    public Long getProductoId() {
        return producto != null ? producto.getId() : null;
    }

    @Transient
    public String getProductoNombre() {
        return producto != null ? producto.getNombre() : null;
    }
}
