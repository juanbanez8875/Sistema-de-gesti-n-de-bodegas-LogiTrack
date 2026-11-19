package com.logitracksa.project.repository;

import com.logitracksa.project.model.entity.Auditoria;
import com.logitracksa.project.model.enums.TipoOperacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    List<Auditoria> findByUsuarioOrderByFechaHoraDesc(String usuario);

    List<Auditoria> findByTipoOperacionOrderByFechaHoraDesc(TipoOperacion tipoOperacion);

    List<Auditoria> findByEntidadOrderByFechaHoraDesc(String entidad);

    List<Auditoria> findByFechaHoraBetweenOrderByFechaHoraDesc(LocalDateTime inicio, LocalDateTime fin);

    List<Auditoria> findAllByOrderByFechaHoraDesc();
}
