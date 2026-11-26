package com.logitracksa.project.repository;

import com.logitracksa.project.model.entity.Movimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    /** Movimientos dentro de un rango de fechas. */
    List<Movimiento> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

    List<Movimiento> findByUsuarioId(Long usuarioId);

    List<Movimiento> findAllByOrderByIdDesc();

    /** Movimientos con paginación y ordenamiento dinámico. */
    Page<Movimiento> findAll(Pageable pageable);

    /** Cantidad de movimientos agrupados por tipo: [tipo, COUNT]. */
    @Query("SELECT m.tipo, COUNT(m) FROM Movimiento m GROUP BY m.tipo")
    List<Object[]> contarPorTipo();

    /** Cantidad de movimientos agrupados por año y mes: [año, mes, COUNT]. */
    @Query("""
            SELECT FUNCTION('YEAR', m.fecha), FUNCTION('MONTH', m.fecha), COUNT(m)
            FROM Movimiento m
            GROUP BY FUNCTION('YEAR', m.fecha), FUNCTION('MONTH', m.fecha)
            ORDER BY FUNCTION('YEAR', m.fecha), FUNCTION('MONTH', m.fecha)
            """)
    List<Object[]> contarPorMes();
}
