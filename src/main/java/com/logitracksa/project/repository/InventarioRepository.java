package com.logitracksa.project.repository;

import com.logitracksa.project.model.entity.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    @Query("SELECT i FROM Inventario i WHERE i.bodega.id = :bodegaId AND i.producto.id = :productoId")
    Optional<Inventario> findByBodegaIdAndProductoId(@Param("bodegaId") Long bodegaId,
                                                       @Param("productoId") Long productoId);

    @Query("SELECT i FROM Inventario i WHERE i.bodega.id = :bodegaId")
    List<Inventario> findByBodegaId(@Param("bodegaId") Long bodegaId);

    /** Inventario con bodega y producto cargados para evitar lazy-loading. */
    @Query("SELECT i FROM Inventario i JOIN FETCH i.bodega JOIN FETCH i.producto ORDER BY i.bodega.nombre, i.producto.nombre")
    List<Inventario> findAllWithBodegaAndProducto();

    /** Total de unidades almacenadas en una bodega (para validar capacidad). */
    @Query("SELECT COALESCE(SUM(i.cantidad), 0) FROM Inventario i WHERE i.bodega.id = :bodegaId")
    int stockTotalEnBodega(@Param("bodegaId") Long bodegaId);

    /** Valor monetario del inventario por bodega: [nombreBodega, SUM(cantidad * precio)]. */
    @Query("""
            SELECT i.bodega.nombre, SUM(i.cantidad * i.producto.precio)
            FROM Inventario i
            GROUP BY i.bodega.nombre
            ORDER BY SUM(i.cantidad * i.producto.precio) DESC
            """)
    List<Object[]> valorPorBodega();
}
