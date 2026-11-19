package com.logitracksa.project.service;

import com.logitracksa.project.model.dto.response.ProductoResponse;
import com.logitracksa.project.model.dto.response.ReporteResponse;
import com.logitracksa.project.model.dto.response.ReporteResponse.ProductoMasMovido;
import com.logitracksa.project.model.dto.response.ReporteResponse.StockPorBodega;
import com.logitracksa.project.model.entity.Inventario;
import com.logitracksa.project.model.entity.Producto;
import com.logitracksa.project.repository.BodegaRepository;
import com.logitracksa.project.repository.DetalleMovimientoRepository;
import com.logitracksa.project.repository.InventarioRepository;
import com.logitracksa.project.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Genera reportes: resumen general (stock por bodega + productos más movidos)
 * y consulta de stock bajo.
 */
@Service
@Transactional(readOnly = true)
public class ReporteService {

    private final InventarioRepository inventarioRepository;
    private final DetalleMovimientoRepository detalleMovimientoRepository;
    private final BodegaRepository bodegaRepository;
    private final ProductoRepository productoRepository;

    public ReporteService(InventarioRepository inventarioRepository,
                            DetalleMovimientoRepository detalleMovimientoRepository,
                            BodegaRepository bodegaRepository,
                            ProductoRepository productoRepository) {
        this.inventarioRepository = inventarioRepository;
        this.detalleMovimientoRepository = detalleMovimientoRepository;
        this.bodegaRepository = bodegaRepository;
        this.productoRepository = productoRepository;
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
