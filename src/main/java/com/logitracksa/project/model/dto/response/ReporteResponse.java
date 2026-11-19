package com.logitracksa.project.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Respuesta del reporte de resumen general.
 * Incluye el stock total por bodega y los productos más movidos.
 */
@Getter
@Setter
@AllArgsConstructor
public class ReporteResponse {

    private List<StockPorBodega> stockTotalPorBodega;
    private List<ProductoMasMovido> productosMasMovidos;
    private int totalBodegas;
    private int totalProductos;
    private int stockTotalGlobal;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class StockPorBodega {
        private Long bodegaId;
        private String bodegaNombre;
        private Long productoId;
        private String productoNombre;
        private Integer cantidad;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ProductoMasMovido {
        private Long productoId;
        private String productoNombre;
        private Integer totalMovido;
    }
}
