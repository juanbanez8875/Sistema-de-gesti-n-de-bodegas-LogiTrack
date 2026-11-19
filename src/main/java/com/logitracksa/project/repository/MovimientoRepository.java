package com.logitracksa.project.repository;

import com.logitracksa.project.model.entity.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    /** Movimientos dentro de un rango de fechas. */
    List<Movimiento> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

    List<Movimiento> findByUsuarioId(Long usuarioId);

    List<Movimiento> findAllByOrderByIdDesc();
}
