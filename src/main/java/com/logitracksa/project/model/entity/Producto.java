package com.logitracksa.project.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.logitracksa.project.listener.AuditEntityListener;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Producto del inventario. El campo {@code stock} representa el total global
 * (suma de todas las bodegas).
 */
@Entity
@Table(name = "producto")
@EntityListeners(AuditEntityListener.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@NoArgsConstructor
public class Producto extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 50)
    private String categoria;

    @Column
    private Integer stock = 0;

    @Column(precision = 10, scale = 2)
    private BigDecimal precio;
}
