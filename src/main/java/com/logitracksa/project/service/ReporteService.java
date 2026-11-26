package com.logitracksa.project.service;

import com.logitracksa.project.model.dto.response.EstadisticasResponse;
import com.logitracksa.project.model.dto.response.ProductoResponse;
import com.logitracksa.project.model.dto.response.ReporteResponse;
import com.logitracksa.project.model.dto.response.ReporteResponse.ProductoMasMovido;
import com.logitracksa.project.model.dto.response.ReporteResponse.StockPorBodega;
import com.logitracksa.project.model.entity.Inventario;
import com.logitracksa.project.model.entity.Producto;
import com.logitracksa.project.repository.AuditoriaRepository;
import com.logitracksa.project.repository.BodegaRepository;
import com.logitracksa.project.repository.DetalleMovimientoRepository;
import com.logitracksa.project.repository.InventarioRepository;
import com.logitracksa.project.repository.MovimientoRepository;
import com.logitracksa.project.repository.ProductoRepository;
import com.logitracksa.project.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Genera reportes: resumen general (stock por bodega + productos más movidos),
 * consulta de stock bajo y estadísticas agregadas (GROUP BY).
 */
@Service
@Transactional(readOnly = true)
public class ReporteService {

    private final InventarioRepository inventarioRepository;
    private final DetalleMovimientoRepository detalleMovimientoRepository;
    private final BodegaRepository bodegaRepository;
    private final ProductoRepository productoRepository;
    private final MovimientoRepository movimientoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaRepository auditoriaRepository;

    public ReporteService(InventarioRepository inventarioRepository,
                            DetalleMovimientoRepository detalleMovimientoRepository,
                            BodegaRepository bodegaRepository,
                            ProductoRepository productoRepository,
                            MovimientoRepository movimientoRepository,
                            UsuarioRepository usuarioRepository,
                            AuditoriaRepository auditoriaRepository) {
        this.inventarioRepository = inventarioRepository;
        this.detalleMovimientoRepository = detalleMovimientoRepository;
        this.bodegaRepository = bodegaRepository;
        this.productoRepository = productoRepository;
        this.movimientoRepository = movimientoRepository;
        this.usuarioRepository = usuarioRepository;
        this.auditoriaRepository = auditoriaRepository;
    }

    /**
     * Resumen general con stock total por bodega, productos más movidos
     * y totales globales.
     */
    public ReporteResponse resumenGeneral() {
        // Stock por bodega
        List<Inventario> inventarios = inventarioRepository.findAllWithBodegaAndProducto();
        List<StockPorBodega> stockPorBodega = inventarios.stream()
                .map(i -> new StockPorBodega(
                        i.getBodega().getId(),
                        i.getBodega().getNombre(),
                        i.getProducto().getId(),
                        i.getProducto().getNombre(),
                        i.getCantidad()
                ))
                .toList();

        // Productos más movidos
        List<Object[]> results = detalleMovimientoRepository.findProductosMasMovidos();
        List<ProductoMasMovido> productosMasMovidos = results.stream()
                .map(r -> new ProductoMasMovido(
                        (Long) r[0],
                        (String) r[1],
                        ((Number) r[2]).intValue()
                ))
                .toList();

        // Totales
        int totalBodegas = (int) bodegaRepository.count();
        int totalProductos = (int) productoRepository.count();
        int stockTotal = productoRepository.findAll().stream()
                .mapToInt(p -> p.getStock() != null ? p.getStock() : 0)
                .sum();

        return new ReporteResponse(stockPorBodega, productosMasMovidos,
                totalBodegas, totalProductos, stockTotal);
    }

    /**
     * Estadísticas agregadas: movimientos por tipo, movimientos por mes,
     * productos por categoría, valor por bodega y valor total del inventario.
     */
    public EstadisticasResponse estadisticas() {
        List<EstadisticasResponse.MovimientoPorTipo> porTipo =
                movimientoRepository.contarPorTipo().stream()
                        .map(r -> new EstadisticasResponse.MovimientoPorTipo(
                                r[0] != null ? r[0].toString() : "?",
                                ((Number) r[1]).longValue()))
                        .toList();

        List<EstadisticasResponse.MovimientoPorMes> porMes =
                movimientoRepository.contarPorMes().stream()
                        .map(r -> new EstadisticasResponse.MovimientoPorMes(
                                ((Number) r[0]).intValue(),
                                ((Number) r[1]).intValue(),
                                ((Number) r[2]).longValue()))
                        .toList();

        List<EstadisticasResponse.ProductoPorCategoria> porCategoria =
                productoRepository.estadisticasPorCategoria().stream()
                        .map(r -> new EstadisticasResponse.ProductoPorCategoria(
                                r[0] != null ? r[0].toString() : "Sin categoría",
                                ((Number) r[1]).longValue(),
                                ((Number) r[2]).intValue()))
                        .toList();

        List<EstadisticasResponse.ValorPorBodega> valorBodega =
                inventarioRepository.valorPorBodega().stream()
                        .map(r -> new EstadisticasResponse.ValorPorBodega(
                                (String) r[0],
                                r[1] != null ? (BigDecimal) r[1] : BigDecimal.ZERO))
                        .toList();

        BigDecimal valorTotal = productoRepository.valorTotalInventario();

        return new EstadisticasResponse(
                porTipo, porMes, porCategoria, valorBodega, valorTotal,
                usuarioRepository.count(),
                movimientoRepository.count(),
                auditoriaRepository.count()
        );
    }

    /** Productos con stock menor a 10 unidades. */
    public List<ProductoResponse> stockBajo() {
        return productoRepository.findByStockLessThan(10).stream()
                .map(this::toResponse)
                .toList();
    }

    private ProductoResponse toResponse(Producto p) {
        return new ProductoResponse(
                p.getId(), p.getNombre(), p.getCategoria(), p.getStock(), p.getPrecio()
        );
    }
}
