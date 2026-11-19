package com.logitracksa.project.listener;

import com.logitracksa.project.model.enums.TipoOperacion;

/**
 * Datos de un evento de auditoría pendiente de guardar.
 * Se acumulan en {@link AuditHolder} durante la transacción y se
 * persisten después del commit.
 */
public record AuditInfo(
        String entidad,
        Long entidadId,
        TipoOperacion tipoOperacion,
        String usuario,
        String valoresAnteriores,
        String valoresNuevos
) {}
