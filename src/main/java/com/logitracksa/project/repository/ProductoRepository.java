package com.logitracksa.project.repository;

import com.logitracksa.project.model.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /** Productos con stock menor al valor indicado (reporte de stock bajo). */
    List<Producto> findByStockLessThan(Integer stock);

    List<Producto> findByCategoria(String categoria);

    /** Búsqueda parcial por nombre, ignorando mayúsculas/minúsculas. */
    @Query("SELECT p FROM Producto p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) ORDER BY p.nombre")
    List<Producto> buscarPorNombre(@Param("nombre") String nombre);

    /** Búsqueda exacta por categoría, ignorando mayúsculas/minúsculas. */
    @Query("SELECT p FROM Producto p WHERE LOWER(p.categoria) = LOWER(:categoria) ORDER BY p.nombre")
    List<Producto> buscarPorCategoria(@Param("categoria") String categoria);

    /** Estadísticas agrupadas por categoría: [nombre, COUNT, SUM(stock)]. */
    @Query("SELECT p.categoria, COUNT(p), COALESCE(SUM(p.stock), 0) FROM Producto p GROUP BY p.categoria ORDER BY p.categoria")
    List<Object[]> estadisticasPorCategoria();

    /** Valor monetario total del inventario global: SUM(stock * precio). */
    @Query("SELECT COALESCE(SUM(p.stock * p.precio), 0) FROM Producto p")
    BigDecimal valorTotalInventario();

    /** Productos con paginación y ordenamiento dinámico. */
    Page<Producto> findAll(Pageable pageable);
}
