package com.logitracksa.project.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Estadísticas generales del sistema: movimientos agrupados por tipo y mes,
 * productos por categoría, valor del inventario por bodega y totales.
 */
@Getter
@Setter
@AllArgsConstructor
public class EstadisticasResponse {

    private List<MovimientoPorTipo> movimientosPorTipo;
    private List<MovimientoPorMes> movimientosPorMes;
    private List<ProductoPorCategoria> productosPorCategoria;
    private List<ValorPorBodega> valorPorBodega;
    private BigDecimal valorTotalInventario;
    private long totalUsuarios;
    private long totalMovimientos;
    private long totalAuditorias;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class MovimientoPorTipo {
        private String tipo;
        private long cantidad;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class MovimientoPorMes {
        private int anio;
        private int mes;
        private long cantidad;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ProductoPorCategoria {
        private String categoria;
        private long productos;
        private int stockTotal;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ValorPorBodega {
        private String bodega;
        private BigDecimal valor;
    }
}
