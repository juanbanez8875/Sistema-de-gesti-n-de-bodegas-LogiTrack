package com.logitracksa.project.repository;

import com.logitracksa.project.model.entity.Bodega;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests de integración para BodegaRepository.
 * Usa @DataJpaTest (Boot 4): levanta solo el contexto JPA con H2 en memoria.
 */
@DataJpaTest
@DisplayName("BodegaRepository — Tests de integración")
class BodegaRepositoryTest {

    @Autowired
    private BodegaRepository bodegaRepository;

    @Autowired
    private TestEntityManager em;

    @BeforeEach
    void setUp() {
        em.persist(build("Bodega Central", "Av. Industrial 100, Guayaquil", 5000));
        em.persist(build("Bodega Norte",   "Km 15 Vía a la Costa, Manta",  3000));
        em.persist(build("Bodega Sur",     "Panamericana Sur 200, Cuenca",  2000));
        em.flush();
    }

    @Test
    @DisplayName("buscar por nombre debe encontrar coincidencias parciales")
    void buscar_porNombre_deberiaEncontrar() {
        List<Bodega> result = bodegaRepository.buscar("central");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("Bodega Central");
    }

    @Test
    @DisplayName("buscar por ubicación debe encontrar coincidencias")
    void buscar_porUbicacion_deberiaEncontrar() {
        List<Bodega> result = bodegaRepository.buscar("guayaquil");
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("buscar con texto en blanco debería devolver todas")
    void buscar_conTextoVacio_devuelveTodas() {
        List<Bodega> result = bodegaRepository.buscar("");
        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("buscar sin coincidencias debería devolver lista vacía")
    void buscar_sinCoincidencias_devuelveVacio() {
        List<Bodega> result = bodegaRepository.buscar("tokyo");
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("count debe reflejar los registros insertados")
    void count_deberiaReflectarInserciones() {
        assertThat(bodegaRepository.count()).isEqualTo(3);
    }

    private Bodega build(String nombre, String ubicacion, int capacidad) {
        Bodega b = new Bodega();
        b.setNombre(nombre);
        b.setUbicacion(ubicacion);
        b.setCapacidad(capacidad);
        return b;
    }
}
