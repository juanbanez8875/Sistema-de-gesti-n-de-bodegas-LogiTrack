package com.logitracksa.project.service;

import com.logitracksa.project.exception.BusinessException;
import com.logitracksa.project.exception.ResourceNotFoundException;
import com.logitracksa.project.model.dto.request.BodegaRequest;
import com.logitracksa.project.model.dto.response.BodegaResponse;
import com.logitracksa.project.model.dto.response.PageResponse;
import com.logitracksa.project.model.entity.Bodega;
import com.logitracksa.project.repository.BodegaRepository;
import com.logitracksa.project.repository.InventarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios de BodegaService con Mockito.
 * No levanta contexto de Spring — 100% en memoria.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BodegaService — Tests unitarios")
class BodegaServiceTest {

    @Mock
    private BodegaRepository bodegaRepository;

    @Mock
    private InventarioRepository inventarioRepository;

    @InjectMocks
    private BodegaService bodegaService;

    private Bodega bodegaEjemplo;

    @BeforeEach
    void setUp() {
        bodegaEjemplo = new Bodega();
        bodegaEjemplo.setId(1L);
        bodegaEjemplo.setNombre("Bodega Central");
        bodegaEjemplo.setUbicacion("Guayaquil");
        bodegaEjemplo.setCapacidad(5000);
        bodegaEjemplo.setEncargado("Carlos");
    }

    // ---- findAll ----

    @Test
    @DisplayName("findAll debe delegar al repositorio y mapear correctamente")
    void findAll_delegaAlRepositorioYMapea() {
        when(bodegaRepository.findAll()).thenReturn(List.of(bodegaEjemplo));

        List<BodegaResponse> result = bodegaService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("Bodega Central");
        verify(bodegaRepository).findAll();
    }

    // ---- findById ----

    @Test
    @DisplayName("findById debe lanzar ResourceNotFoundException si no existe")
    void findById_noExiste_lanzaNotFoundException() {
        when(bodegaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bodegaService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("findById debe devolver la bodega si existe")
    void findById_existe_devuelveBodega() {
        when(bodegaRepository.findById(1L)).thenReturn(Optional.of(bodegaEjemplo));

        BodegaResponse result = bodegaService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNombre()).isEqualTo("Bodega Central");
    }

    // ---- create ----

    @Test
    @DisplayName("create debe persistir y devolver la nueva bodega")
    void create_persisteYDevuelve() {
        BodegaRequest req = new BodegaRequest("Nueva Bodega", "Quito", 1000, "Ana");
        when(bodegaRepository.save(any(Bodega.class))).thenAnswer(inv -> {
            Bodega b = inv.getArgument(0);
            b.setId(2L);
            return b;
        });

        BodegaResponse result = bodegaService.create(req);

        assertThat(result.getNombre()).isEqualTo("Nueva Bodega");
        assertThat(result.getUbicacion()).isEqualTo("Quito");
        verify(bodegaRepository).save(any(Bodega.class));
    }

    // ---- update con validación de capacidad ----

    @Test
    @DisplayName("update debe lanzar BusinessException si capacidad < stock actual")
    void update_capacidadMenorQueStock_lanzaBusinessException() {
        BodegaRequest req = new BodegaRequest("Bodega Central", "Guayaquil", 50, "Carlos");
        when(bodegaRepository.findById(1L)).thenReturn(Optional.of(bodegaEjemplo));
        when(inventarioRepository.stockTotalEnBodega(1L)).thenReturn(200);

        assertThatThrownBy(() -> bodegaService.update(1L, req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("capacidad");
    }

    @Test
    @DisplayName("update debe permitir reducir capacidad si stock lo permite")
    void update_capacidadMayorQueStock_actualizaOk() {
        BodegaRequest req = new BodegaRequest("Bodega Central", "Guayaquil", 300, "Carlos");
        when(bodegaRepository.findById(1L)).thenReturn(Optional.of(bodegaEjemplo));
        when(inventarioRepository.stockTotalEnBodega(1L)).thenReturn(200);
        when(bodegaRepository.save(any())).thenReturn(bodegaEjemplo);

        BodegaResponse result = bodegaService.update(1L, req);

        assertThat(result).isNotNull();
        verify(bodegaRepository).save(any(Bodega.class));
    }

    // ---- delete ----

    @Test
    @DisplayName("delete debe lanzar ResourceNotFoundException si la bodega no existe")
    void delete_noExiste_lanzaNotFoundException() {
        when(bodegaRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> bodegaService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(bodegaRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("delete debe llamar a deleteById cuando existe")
    void delete_existe_eliminaCorrectamente() {
        when(bodegaRepository.existsById(1L)).thenReturn(true);

        bodegaService.delete(1L);

        verify(bodegaRepository).deleteById(1L);
    }

    // ---- paginación ----

    @Test
    @DisplayName("findAllPaged debe devolver PageResponse con el contenido correcto")
    void findAllPaged_devuelvePageResponse() {
        var pageImpl = new PageImpl<>(List.of(bodegaEjemplo));
        when(bodegaRepository.findAll(any(Pageable.class))).thenReturn(pageImpl);

        PageResponse<BodegaResponse> result = bodegaService.findAllPaged(0, 10, "nombre", "ASC");

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.isLast()).isTrue();
    }
}
