package com.logitracksa.project.service;

import com.logitracksa.project.exception.BusinessException;
import com.logitracksa.project.exception.ResourceNotFoundException;
import com.logitracksa.project.model.dto.request.DetalleMovimientoRequest;
import com.logitracksa.project.model.dto.request.MovimientoRequest;
import com.logitracksa.project.model.dto.response.DetalleMovimientoResponse;
import com.logitracksa.project.model.dto.response.MovimientoResponse;
import com.logitracksa.project.model.entity.*;
import com.logitracksa.project.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Registro de movimientos de inventario y actualización de stock.
 * Cada movimiento actualiza el stock global (Producto.stock) y el stock
 * por bodega (Inventario.cantidad) según el tipo de operación.
 */
@Service
@Transactional
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final BodegaRepository bodegaRepository;
    private final ProductoRepository productoRepository;
    private final InventarioRepository inventarioRepository;
    private final UsuarioRepository usuarioRepository;

    public MovimientoService(MovimientoRepository movimientoRepository,
                               BodegaRepository bodegaRepository,
                               ProductoRepository productoRepository,
                               InventarioRepository inventarioRepository,
                               UsuarioRepository usuarioRepository) {
        this.movimientoRepository = movimientoRepository;
        this.bodegaRepository = bodegaRepository;
        this.productoRepository = productoRepository;
        this.inventarioRepository = inventarioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponse> findAll() {
        return movimientoRepository.findAllByOrderByIdDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MovimientoResponse findById(Long id) {
        Movimiento mov = movimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado con id: " + id));
        return toResponse(mov);
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponse> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin) {
        return movimientoRepository.findByFechaBetween(inicio, fin).stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Registra un movimiento, valida bodegas según el tipo, crea los detalles
     * y actualiza el stock global y por bodega.
     */
    public MovimientoResponse registrar(MovimientoRequest request, String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        Movimiento mov = new Movimiento();
        mov.setFecha(LocalDateTime.now());
        mov.setTipo(request.getTipo());
        mov.setUsuario(usuario);

        // Validar y asignar bodegas según el tipo
        switch (request.getTipo()) {
            case ENTRADA -> {
                if (request.getBodegaDestinoId() == null)
                    throw new BusinessException("ENTRADA requiere bodega destino");
                mov.setBodegaDestino(obtenerBodega(request.getBodegaDestinoId()));
            }
            case SALIDA -> {
                if (request.getBodegaOrigenId() == null)
                    throw new BusinessException("SALIDA requiere bodega origen");
                mov.setBodegaOrigen(obtenerBodega(request.getBodegaOrigenId()));
            }
            case TRANSFERENCIA -> {
                if (request.getBodegaOrigenId() == null || request.getBodegaDestinoId() == null)
                    throw new BusinessException("TRANSFERENCIA requiere bodega origen y destino");
                if (request.getBodegaOrigenId().equals(request.getBodegaDestinoId()))
                    throw new BusinessException("La bodega origen y destino no pueden ser la misma");
                mov.setBodegaOrigen(obtenerBodega(request.getBodegaOrigenId()));
                mov.setBodegaDestino(obtenerBodega(request.getBodegaDestinoId()));
            }
        }

        // Crear detalles y actualizar stock
        for (DetalleMovimientoRequest det : request.getDetalles()) {
            Producto producto = productoRepository.findById(det.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Producto no encontrado con id: " + det.getProductoId()));

            DetalleMovimiento detalle = new DetalleMovimiento();
            detalle.setProducto(producto);
            detalle.setCantidad(det.getCantidad());
            mov.addDetalle(detalle);

            actualizarStock(mov, producto, det.getCantidad());
            productoRepository.save(producto);
        }

        movimientoRepository.save(mov);
        return toResponse(mov);
    }

    /**
     * Actualiza el stock global (Producto) y por bodega (Inventario)
     * según el tipo de movimiento.
     */
    private void actualizarStock(Movimiento mov, Producto producto, int cantidad) {
        switch (mov.getTipo()) {
            case ENTRADA -> {
                // Stock global aumenta
                producto.setStock(safe(producto.getStock()) + cantidad);
                // Stock por bodega aumenta
                ajustarInventario(mov.getBodegaDestino(), producto, cantidad, true);
            }
            case SALIDA -> {
                // Validar stock global
                int nuevo = safe(producto.getStock()) - cantidad;
                if (nuevo < 0)
                    throw new BusinessException("Stock global insuficiente para " + producto.getNombre());
                producto.setStock(nuevo);
                // Validar y ajustar stock por bodega
                ajustarInventario(mov.getBodegaOrigen(), producto, cantidad, false);
            }
            case TRANSFERENCIA -> {
                // Stock global no cambia: solo se mueve entre bodegas
                ajustarInventario(mov.getBodegaOrigen(), producto, cantidad, false);
                ajustarInventario(mov.getBodegaDestino(), producto, cantidad, true);
            }
        }
    }

    /**
     * Aumenta o disminuye el inventario de una bodega para un producto.
     * Si es decremento y no hay suficiente stock, lanza BusinessException.
     * Si no existe el registro, lo crea (solo para incrementos).
     */
    private void ajustarInventario(Bodega bodega, Producto producto, int cantidad, boolean entrada) {
        Inventario inv = inventarioRepository
                .findByBodegaIdAndProductoId(bodega.getId(), producto.getId())
                .orElseGet(() -> {
                    Inventario nuevo = new Inventario();
                    nuevo.setBodega(bodega);
                    nuevo.setProducto(producto);
                    nuevo.setCantidad(0);
                    return nuevo;
                });

        int actual = safe(inv.getCantidad());
        if (!entrada && actual < cantidad) {
            throw new BusinessException("Stock insuficiente en " + bodega.getNombre()
                    + " para " + producto.getNombre()
                    + " (disponible: " + actual + ", requerido: " + cantidad + ")");
        }
        inv.setCantidad(entrada ? actual + cantidad : actual - cantidad);
        inventarioRepository.save(inv);
    }

    private Bodega obtenerBodega(Long id) {
        return bodegaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bodega no encontrada con id: " + id));
    }

    private int safe(Integer val) {
        return val != null ? val : 0;
    }

    private MovimientoResponse toResponse(Movimiento mov) {
        List<DetalleMovimientoResponse> detalles = mov.getDetalles().stream()
                .map(d -> new DetalleMovimientoResponse(
                        d.getProductoId(),
                        d.getProductoNombre(),
                        d.getCantidad()
                ))
                .toList();

        return new MovimientoResponse(
                mov.getId(),
                mov.getFecha(),
                mov.getTipo(),
                mov.getUsuarioUsername(),
                mov.getBodegaOrigen() != null ? mov.getBodegaOrigen().getNombre() : null,
                mov.getBodegaDestino() != null ? mov.getBodegaDestino().getNombre() : null,
                detalles
        );
    }
}
