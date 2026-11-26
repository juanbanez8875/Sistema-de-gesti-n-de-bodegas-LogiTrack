package com.logitracksa.project.service;

import com.logitracksa.project.exception.BusinessException;
import com.logitracksa.project.exception.ResourceNotFoundException;
import com.logitracksa.project.model.dto.request.BodegaRequest;
import com.logitracksa.project.model.dto.response.BodegaResponse;
import com.logitracksa.project.model.dto.response.PageResponse;
import com.logitracksa.project.model.entity.Bodega;
import com.logitracksa.project.repository.BodegaRepository;
import com.logitracksa.project.repository.InventarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * CRUD de bodegas, búsqueda y paginación.
 * Al actualizar la capacidad, valida que no sea menor al stock actual.
 */
@Service
@Transactional
public class BodegaService {

    private final BodegaRepository bodegaRepository;
    private final InventarioRepository inventarioRepository;

    public BodegaService(BodegaRepository bodegaRepository,
                           InventarioRepository inventarioRepository) {
        this.bodegaRepository = bodegaRepository;
        this.inventarioRepository = inventarioRepository;
    }

    @Transactional(readOnly = true)
    public List<BodegaResponse> findAll() {
        return bodegaRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BodegaResponse findById(Long id) {
        return toResponse(getBodega(id));
    }

    /** Paginación y ordenamiento dinámico. */
    @Transactional(readOnly = true)
    public PageResponse<BodegaResponse> findAllPaged(int page, int size, String sort, String dir) {
        Sort.Direction direction = "DESC".equalsIgnoreCase(dir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));
        Page<Bodega> result = bodegaRepository.findAll(pageable);
        return new PageResponse<>(
                result.getContent().stream().map(this::toResponse).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isLast()
        );
    }

    /** Búsqueda parcial por nombre o ubicación (LIKE, insensible a mayúsculas). */
    @Transactional(readOnly = true)
    public List<BodegaResponse> buscar(String filtro) {
        return bodegaRepository.buscar(filtro).stream()
                .map(this::toResponse)
                .toList();
    }

    public BodegaResponse create(BodegaRequest request) {
        Bodega b = new Bodega();
        applyRequest(b, request);
        bodegaRepository.save(b);
        return toResponse(b);
    }

    /**
     * Actualiza la bodega. Si la nueva capacidad es menor al stock actual
     * almacenado, lanza BusinessException.
     */
    public BodegaResponse update(Long id, BodegaRequest request) {
        Bodega b = getBodega(id);

        if (request.getCapacidad() != null && request.getCapacidad() > 0) {
            int stockActual = inventarioRepository.stockTotalEnBodega(id);
            if (request.getCapacidad() < stockActual) {
                throw new BusinessException(
                        "No se puede reducir la capacidad a " + request.getCapacidad()
                        + " porque la bodega tiene " + stockActual + " unidades almacenadas"
                );
            }
        }

        applyRequest(b, request);
        bodegaRepository.save(b);
        return toResponse(b);
    }

    public void delete(Long id) {
        if (!bodegaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Bodega no encontrada con id: " + id);
        }
        bodegaRepository.deleteById(id);
    }

    // ----- helpers -----

    public Bodega getBodega(Long id) {
        return bodegaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bodega no encontrada con id: " + id));
    }

    private void applyRequest(Bodega b, BodegaRequest req) {
        b.setNombre(req.getNombre());
        b.setUbicacion(req.getUbicacion());
        b.setCapacidad(req.getCapacidad());
        b.setEncargado(req.getEncargado());
    }

    public BodegaResponse toResponse(Bodega b) {
        return new BodegaResponse(
                b.getId(), b.getNombre(), b.getUbicacion(), b.getCapacidad(), b.getEncargado()
        );
    }
}
