package com.logitracksa.project.service;

import com.logitracksa.project.exception.ResourceNotFoundException;
import com.logitracksa.project.model.dto.request.ProductoRequest;
import com.logitracksa.project.model.dto.response.ProductoResponse;
import com.logitracksa.project.model.entity.Producto;
import com.logitracksa.project.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios de ProductoService con Mockito.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductoService — Tests unitarios")
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    private Producto productoEjemplo;

    @BeforeEach
    void setUp() {
        productoEjemplo = new Producto();
        productoEjemplo.setId(1L);
        productoEjemplo.setNombre("Arroz 5kg");
        productoEjemplo.setCategoria("Alimentos");
        productoEjemplo.setStock(150);
        productoEjemplo.setPrecio(new BigDecimal("4.50"));
    }

    // ---- findAll ----

    @Test
    @DisplayName("findAll debe retornar todos los productos")
    void findAll_retornaTodos() {
        when(productoRepository.findAll()).thenReturn(List.of(productoEjemplo));

        List<ProductoResponse> result = productoService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("Arroz 5kg");
    }

    // ---- findById ----

    @Test
    @DisplayName("findById debe lanzar ResourceNotFoundException si no existe")
    void findById_noExiste_lanzaException() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("findById debe devolver el producto si existe")
    void findById_existe_devuelve() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoEjemplo));

        ProductoResponse result = productoService.findById(1L);

        assertThat(result.getNombre()).isEqualTo("Arroz 5kg");
        assertThat(result.getStock()).isEqualTo(150);
    }

    // ---- create ----

    @Test
    @DisplayName("create debe llamar a save y devolver el DTO")
    void create_guardaYDevuelve() {
        ProductoRequest req = new ProductoRequest("Fideos", "Alimentos", 200, new BigDecimal("1.20"));
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> {
            Producto p = inv.getArgument(0);
            p.setId(2L);
            return p;
        });

        ProductoResponse result = productoService.create(req);

        assertThat(result.getNombre()).isEqualTo("Fideos");
        verify(productoRepository).save(any(Producto.class));
    }

    // ---- update ----

    @Test
    @DisplayName("update debe modificar los campos del producto existente")
    void update_modificaCorrectamente() {
        ProductoRequest req = new ProductoRequest("Arroz Premium", "Alimentos", 100, new BigDecimal("5.00"));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoEjemplo));
        when(productoRepository.save(any())).thenReturn(productoEjemplo);

        ProductoResponse result = productoService.update(1L, req);

        assertThat(result).isNotNull();
        verify(productoRepository).save(any(Producto.class));
    }

    // ---- delete ----

    @Test
    @DisplayName("delete debe lanzar excepción si el producto no existe")
    void delete_noExiste_lanzaException() {
        when(productoRepository.existsById(5L)).thenReturn(false);

        assertThatThrownBy(() -> productoService.delete(5L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(productoRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("delete debe llamar a deleteById cuando el producto existe")
    void delete_existe_llama_deleteById() {
        when(productoRepository.existsById(1L)).thenReturn(true);

        productoService.delete(1L);

        verify(productoRepository).deleteById(1L);
    }

    // ---- findStockBajo ----

    @Test
    @DisplayName("findStockBajo solo debe incluir productos con stock < 10")
    void findStockBajo_soloDevuelveProductosBajos() {
        Producto bajo = new Producto();
        bajo.setId(5L); bajo.setNombre("Escaso"); bajo.setStock(3); bajo.setPrecio(BigDecimal.ONE);
        when(productoRepository.findByStockLessThan(10)).thenReturn(List.of(bajo));

        List<ProductoResponse> result = productoService.findStockBajo();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStock()).isLessThan(10);
    }

    // ---- buscarPorNombre ----

    @Test
    @DisplayName("buscarPorNombre debe delegar al repositorio")
    void buscarPorNombre_delegaAlRepo() {
        when(productoRepository.buscarPorNombre("arro")).thenReturn(List.of(productoEjemplo));

        List<ProductoResponse> result = productoService.buscarPorNombre("arro");

        assertThat(result).hasSize(1);
        verify(productoRepository).buscarPorNombre("arro");
    }
}
