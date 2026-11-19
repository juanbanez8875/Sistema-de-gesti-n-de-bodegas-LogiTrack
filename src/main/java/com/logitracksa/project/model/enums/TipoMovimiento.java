package com.logitracksa.project.model.enums;

/**
 * Tipos de movimiento de inventario.
 */
public enum TipoMovimiento {
    /** Ingreso de productos a una bodega (solo bodega destino) */
    ENTRADA,
    /** Salida de productos de una bodega (solo bodega origen) */
    SALIDA,
    /** Traslado de productos entre dos bodegas (origen y destino) */
    TRANSFERENCIA
}
