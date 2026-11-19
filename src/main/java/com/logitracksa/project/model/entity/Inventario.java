package com.logitracksa.project.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Inventario: stock de un producto en una bodega específica.
 * No se audita directamente; sus cambios provienen de los movimientos.
 */
@Entity
@Table(
    name = "inventario",
    uniqueConstraints = @UniqueConstraint(columnNames = {"bodega_id", "producto_id"})
)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@NoArgsConstructor
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bodega_id", nullable = false)
    private Bodega bodega;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column
    private Integer cantidad = 0;

    /** Devuelve el id de la bodega sin disparar lazy-loading. */
    @Transient
    public Long getBodegaId() {
        return bodega != null ? bodega.getId() : null;
    }

    /** Devuelve el id del producto sin disparar lazy-loading. */
    @Transient
    public Long getProductoId() {
        return producto != null ? producto.getId() : null;
    }
}
