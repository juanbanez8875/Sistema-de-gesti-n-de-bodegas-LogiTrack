package com.logitracksa.project.repository;

import com.logitracksa.project.model.entity.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests de integración para ProductoRepository.
 * Usa @DataJpaTest (Boot 4): levanta solo el contexto JPA con H2 en memoria.
 */
@DataJpaTest
@DisplayName("ProductoRepository — Tests de integración")
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private TestEntityManager em;

    @BeforeEach
    void setUp() {
        em.persist(build("Arroz 5kg",   "Alimentos",   150, "4.50"));
        em.persist(build("Aceite 1L",   "Alimentos",    80, "2.80"));
        em.persist(build("Detergente",  "Limpieza",      8, "3.20"));
        em.persist(build("Atún Lata",   "Enlatados",     5, "1.80"));
        em.persist(build("Fideos 500g", "Alimentos",   300, "1.20"));
        em.persist(build("Azúcar 1kg",  "Alimentos",     0, "1.10"));
        em.flush();
    }

    // ---- findByStockLessThan ----

    @Test
    @DisplayName("findByStockLessThan debe devolver productos con stock bajo el umbral")
    void findByStockLessThan_deberiaRetornarStockBajo() {
        List<Producto> result = productoRepository.findByStockLessThan(10);
        assertThat(result).hasSize(3);
        assertThat(result).allMatch(p -> p.getStock() < 10);
    }

    @Test
    @DisplayName("findByStockLessThan con umbral 0 no devuelve nada")
    void findByStockLessThan_conCero_noDevuelveNada() {
        List<Producto> result = productoRepository.findByStockLessThan(0);
        assertThat(result).isEmpty();
    }

    // ---- buscarPorNombre ----

    @Test
    @DisplayName("buscarPorNombre debe encontrar coincidencias parciales")
    void buscarPorNombre_deberiaEncontrarCoincidenciasParciales() {
        List<Producto> result = productoRepository.buscarPorNombre("arro");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("Arroz 5kg");
    }

    @Test
    @DisplayName("buscarPorNombre debe ser insensible a mayúsculas")
    void buscarPorNombre_deberiaSerInsensible() {
        List<Producto> upper = productoRepository.buscarPorNombre("ACEITE");
        List<Producto> lower = productoRepository.buscarPorNombre("aceite");
        assertThat(upper).hasSize(1);
        assertThat(lower).hasSize(1);
    }

    @Test
    @DisplayName("buscarPorNombre sin resultados devuelve lista vacía")
    void buscarPorNombre_sinResultados_devuelveVacio() {
        List<Producto> result = productoRepository.buscarPorNombre("xxxxxxxx");
        assertThat(result).isEmpty();
    }

    // ---- buscarPorCategoria ----

    @Test
    @DisplayName("buscarPorCategoria debe retornar los productos de esa categoría")
    void buscarPorCategoria_deberiaRetornarProductosDeLaCategoria() {
        List<Producto> result = productoRepository.buscarPorCategoria("Alimentos");
        assertThat(result).hasSize(4);
    }

    @Test
    @DisplayName("buscarPorCategoria debe ser insensible a mayúsculas")
    void buscarPorCategoria_insensibleAMayusculas() {
        List<Producto> upper = productoRepository.buscarPorCategoria("LIMPIEZA");
        List<Producto> lower = productoRepository.buscarPorCategoria("limpieza");
        assertThat(upper).hasSize(1);
        assertThat(lower).hasSize(1);
    }

    // ---- estadisticasPorCategoria ----

    @Test
    @DisplayName("estadisticasPorCategoria debe agrupar correctamente")
    void estadisticasPorCategoria_deberiaAgrupar() {
        List<Object[]> result = productoRepository.estadisticasPorCategoria();
        assertThat(result).isNotEmpty();
        result.stream()
              .filter(r -> "Alimentos".equals(r[0]))
              .findFirst()
              .ifPresent(r -> assertThat(((Number) r[1]).longValue()).isEqualTo(4));
    }

    // ---- valorTotalInventario ----

    @Test
    @DisplayName("valorTotalInventario debe devolver un valor mayor a cero")
    void valorTotalInventario_deberiaSuperarCero() {
        BigDecimal valor = productoRepository.valorTotalInventario();
        assertThat(valor).isGreaterThan(BigDecimal.ZERO);
    }

    // ---- paginación ----

    @Test
    @DisplayName("paginación debe respetar el tamaño de página")
    void findAll_paginado_respetaTamanio() {
        Page<Producto> page = productoRepository.findAll(
                PageRequest.of(0, 3, Sort.by("nombre")));
        assertThat(page.getContent()).hasSize(3);
        assertThat(page.getTotalElements()).isEqualTo(6);
        assertThat(page.getTotalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("segunda página debe tener los elementos restantes")
    void findAll_paginado_segundaPagina() {
        Page<Producto> page = productoRepository.findAll(
                PageRequest.of(1, 4, Sort.by("nombre")));
        assertThat(page.getContent()).hasSize(2);
        assertThat(page.isLast()).isTrue();
    }

    // ---- Helper ----

    private Producto build(String nombre, String categoria, int stock, String precio) {
        Producto p = new Producto();
        p.setNombre(nombre);
        p.setCategoria(categoria);
        p.setStock(stock);
        p.setPrecio(new BigDecimal(precio));
        return p;
    }
}
