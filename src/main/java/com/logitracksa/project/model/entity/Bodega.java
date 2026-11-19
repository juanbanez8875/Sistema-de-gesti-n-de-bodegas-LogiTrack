package com.logitracksa.project.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.logitracksa.project.listener.AuditEntityListener;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Bodega o almacén donde se guardan productos.
 */
@Entity
@Table(name = "bodega")
@EntityListeners(AuditEntityListener.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@NoArgsConstructor
public class Bodega extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 200)
    private String ubicacion;

    @Column
    private Integer capacidad;

    @Column(length = 100)
    private String encargado;
}
