package com.logitracksa.project.service;

import com.logitracksa.project.listener.AuditInfo;
import com.logitracksa.project.model.dto.response.AuditoriaResponse;
import com.logitracksa.project.model.entity.Auditoria;
import com.logitracksa.project.model.enums.TipoOperacion;
import com.logitracksa.project.repository.AuditoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Persiste registros de auditoría y ofrece consultas sobre ellos.
 * El método {@link #registrar(AuditInfo)} usa REQUIRES_NEW porque se
 * invoca después del commit de la transacción principal.
 */
@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaService(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(AuditInfo info) {
        Auditoria audit = new Auditoria();
        audit.setTipoOperacion(info.tipoOperacion());
        audit.setFechaHora(LocalDateTime.now());
        audit.setUsuario(info.usuario());
        audit.setEntidad(info.entidad());
        audit.setEntidadId(info.entidadId());
        audit.setValoresAnteriores(info.valoresAnteriores());
        audit.setValoresNuevos(info.valoresNuevos());
        auditoriaRepository.save(audit);
    }

    @Transactional(readOnly = true)
    public List<AuditoriaResponse> findAll() {
        return auditoriaRepository.findAllByOrderByFechaHoraDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AuditoriaResponse> findByUsuario(String usuario) {
        return auditoriaRepository.findByUsuarioOrderByFechaHoraDesc(usuario).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AuditoriaResponse> findByTipoOperacion(TipoOperacion tipo) {
        return auditoriaRepository.findByTipoOperacionOrderByFechaHoraDesc(tipo).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AuditoriaResponse> findByEntidad(String entidad) {
        return auditoriaRepository.findByEntidadOrderByFechaHoraDesc(entidad).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AuditoriaResponse> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin) {
        return auditoriaRepository.findByFechaHoraBetweenOrderByFechaHoraDesc(inicio, fin).stream()
                .map(this::toResponse)
                .toList();
    }

    private AuditoriaResponse toResponse(Auditoria a) {
        return new AuditoriaResponse(
                a.getId(),
                a.getTipoOperacion(),
                a.getFechaHora(),
                a.getUsuario(),
                a.getEntidad(),
                a.getEntidadId(),
                a.getValoresAnteriores(),
                a.getValoresNuevos()
        );
    }
}
