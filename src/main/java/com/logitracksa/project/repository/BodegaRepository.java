package com.logitracksa.project.repository;

import com.logitracksa.project.model.entity.Bodega;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BodegaRepository extends JpaRepository<Bodega, Long> {

    /** Búsqueda parcial por nombre o ubicación, ignorando mayúsculas/minúsculas. */
    @Query("""
            SELECT b FROM Bodega b
            WHERE LOWER(b.nombre) LIKE LOWER(CONCAT('%', :filtro, '%'))
               OR LOWER(b.ubicacion) LIKE LOWER(CONCAT('%', :filtro, '%'))
            ORDER BY b.nombre
            """)
    List<Bodega> buscar(@Param("filtro") String filtro);

    /** Bodegas con paginación y ordenamiento dinámico. */
    Page<Bodega> findAll(Pageable pageable);
}
