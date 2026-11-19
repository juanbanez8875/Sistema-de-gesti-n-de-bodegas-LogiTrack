package com.logitracksa.project.service;

import com.logitracksa.project.exception.ResourceNotFoundException;
import com.logitracksa.project.model.dto.request.ProductoRequest;
import com.logitracksa.project.model.dto.response.ProductoResponse;
import com.logitracksa.project.model.entity.Producto;
import com.logitracksa.project.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * CRUD de productos y consulta de stock bajo.
 */
@Service
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> findAll() {
        return productoRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductoResponse findById(Long id) {
        return toResponse(getProducto(id));
    }

    /** Productos con stock menor a 10 unidades. */
    @Transactional(readOnly = true)
    public List<ProductoResponse> findStockBajo() {
        return productoRepository.findByStockLessThan(10).stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductoResponse create(ProductoRequest request) {
        Producto p = new Producto();
        applyRequest(p, request);
        productoRepository.save(p);
        return toResponse(p);
    }

    public ProductoResponse update(Long id, ProductoRequest request) {
        Producto p = getProducto(id);
        applyRequest(p, request);
        productoRepository.save(p);
        return toResponse(p);
    }

    public void delete(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado con id: " + id);
        }
        productoRepository.deleteById(id);
    }

    // ----- helpers -----

    Producto getProducto(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
    }

    private void applyRequest(Producto p, ProductoRequest req) {
        p.setNombre(req.getNombre());
        p.setCategoria(req.getCategoria());
        p.setStock(req.getStock());
        p.setPrecio(req.getPrecio());
    }

    private ProductoResponse toResponse(Producto p) {
        return new ProductoResponse(
                p.getId(), p.getNombre(), p.getCategoria(), p.getStock(), p.getPrecio()
        );
    }
}
