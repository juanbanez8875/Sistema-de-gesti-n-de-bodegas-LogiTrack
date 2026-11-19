package com.logitracksa.project.repository;

import com.logitracksa.project.model.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /** Productos con stock menor al valor indicado (reporte de stock bajo). */
    List<Producto> findByStockLessThan(Integer stock);

    List<Producto> findByCategoria(String categoria);
}
