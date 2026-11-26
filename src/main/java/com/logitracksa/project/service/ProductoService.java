package com.logitracksa.project.service;

import com.logitracksa.project.exception.ResourceNotFoundException;
import com.logitracksa.project.model.dto.request.ProductoRequest;
import com.logitracksa.project.model.dto.response.PageResponse;
import com.logitracksa.project.model.dto.response.ProductoResponse;
import com.logitracksa.project.model.entity.Producto;
import com.logitracksa.project.repository.ProductoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * CRUD de productos, búsqueda por nombre/categoría y consulta de stock bajo.
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

    /** Paginación y ordenamiento. Parámetros: page, size, sort (campo), dir (ASC/DESC). */
    @Transactional(readOnly = true)
    public PageResponse<ProductoResponse> findAllPaged(int page, int size, String sort, String dir) {
        Sort.Direction direction = "DESC".equalsIgnoreCase(dir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));
        Page<Producto> result = productoRepository.findAll(pageable);
        return new PageResponse<>(
                result.getContent().stream().map(this::toResponse).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isLast()
        );
    }

    /** Búsqueda parcial por nombre (LIKE, insensible a mayúsculas). */
    @Transactional(readOnly = true)
    public List<ProductoResponse> buscarPorNombre(String nombre) {
        return productoRepository.buscarPorNombre(nombre).stream()
                .map(this::toResponse)
                .toList();
    }

    /** Búsqueda exacta por categoría (insensible a mayúsculas). */
    @Transactional(readOnly = true)
    public List<ProductoResponse> buscarPorCategoria(String categoria) {
        return productoRepository.buscarPorCategoria(categoria).stream()
                .map(this::toResponse)
                .toList();
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

    public Producto getProducto(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
    }

    private void applyRequest(Producto p, ProductoRequest req) {
        p.setNombre(req.getNombre());
        p.setCategoria(req.getCategoria());
        p.setStock(req.getStock());
        p.setPrecio(req.getPrecio());
    }

    public ProductoResponse toResponse(Producto p) {
        return new ProductoResponse(
                p.getId(), p.getNombre(), p.getCategoria(), p.getStock(), p.getPrecio()
        );
    }
}
