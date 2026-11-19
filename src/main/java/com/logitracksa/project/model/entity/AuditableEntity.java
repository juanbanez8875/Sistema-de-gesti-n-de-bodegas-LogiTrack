package com.logitracksa.project.model.entity;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

/**
 * Clase base para entidades auditadas.
 * Mantiene una instantánea (snapshot) en memoria del estado de la entidad
 * al cargarla desde la base de datos, para comparar valores anteriores/nuevos
 * en las operaciones de UPDATE y DELETE.
 *
 * El campo {@code auditSnapshot} es transitorio: no se persiste en la BD.
 */
@MappedSuperclass
@Getter
@Setter
public abstract class AuditableEntity implements java.io.Serializable {

    /** Snapshot JSON del estado anterior (capturado en @PostLoad). */
    @Transient
    private String auditSnapshot;
}
