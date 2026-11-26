package com.logitracksa.project.repository;

import com.logitracksa.project.model.entity.DetalleMovimiento;
import com.logitracksa.project.model.entity.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleMovimientoRepository extends JpaRepository<DetalleMovimiento, Long> {

    /**
     * Productos más movidos: id, nombre y suma total de cantidades.
     * Ordenado de mayor a menor.
     */
    @Query("""
            SELECT d.producto.id, d.producto.nombre, SUM(d.cantidad)
            FROM DetalleMovimiento d
            GROUP BY d.producto.id, d.producto.nombre
            ORDER BY SUM(d.cantidad) DESC
            """)
    List<Object[]> findProductosMasMovidos();

    /** Historial de movimientos de un producto (trazabilidad), del más reciente al más antiguo. */
    @Query("SELECT DISTINCT d.movimiento FROM DetalleMovimiento d WHERE d.producto.id = :productoId ORDER BY d.movimiento.fecha DESC")
    List<Movimiento> findMovimientosPorProducto(@Param("productoId") Long productoId);
}
