package com.logitracksa.project.service;

import com.logitracksa.project.exception.ResourceNotFoundException;
import com.logitracksa.project.model.dto.request.BodegaRequest;
import com.logitracksa.project.model.dto.response.BodegaResponse;
import com.logitracksa.project.model.entity.Bodega;
import com.logitracksa.project.repository.BodegaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * CRUD de bodegas.
 */
@Service
@Transactional
public class BodegaService {

    private final BodegaRepository bodegaRepository;

    public BodegaService(BodegaRepository bodegaRepository) {
        this.bodegaRepository = bodegaRepository;
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

    public BodegaResponse create(BodegaRequest request) {
        Bodega b = new Bodega();
        applyRequest(b, request);
        bodegaRepository.save(b);
        return toResponse(b);
    }

    public BodegaResponse update(Long id, BodegaRequest request) {
        Bodega b = getBodega(id);
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

    Bodega getBodega(Long id) {
        return bodegaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bodega no encontrada con id: " + id));
    }

    private void applyRequest(Bodega b, BodegaRequest req) {
        b.setNombre(req.getNombre());
        b.setUbicacion(req.getUbicacion());
        b.setCapacidad(req.getCapacidad());
        b.setEncargado(req.getEncargado());
    }

    private BodegaResponse toResponse(Bodega b) {
        return new BodegaResponse(
                b.getId(), b.getNombre(), b.getUbicacion(), b.getCapacidad(), b.getEncargado()
        );
    }
}
