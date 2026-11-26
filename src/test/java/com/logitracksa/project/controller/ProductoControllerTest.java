package com.logitracksa.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.logitracksa.project.exception.ResourceNotFoundException;
import com.logitracksa.project.model.dto.request.ProductoRequest;
import com.logitracksa.project.model.dto.response.ProductoResponse;
import com.logitracksa.project.security.JwtTokenProvider;
import com.logitracksa.project.service.ProductoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de controlador para ProductoController.
 *
 * @WebMvcTest: levanta solo la capa web (filtros, controladores, seguridad).
 * @MockitoBean: proporciona mocks de los servicios y el JwtTokenProvider.
 * @WithMockUser: simula un usuario autenticado en Spring Security.
 */
@WebMvcTest(ProductoController.class)
@DisplayName("ProductoController — Tests de integración web")
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // ObjectMapper instanciado directamente: no es un bean en @WebMvcTest de Boot 4
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @MockitoBean
    private ProductoService productoService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    private static final ProductoResponse RESPONSE = new ProductoResponse(
            1L, "Arroz 5kg", "Alimentos", 150, new BigDecimal("4.50"));

    // ---- GET /productos ----

    @Test
    @WithMockUser
    @DisplayName("GET /productos debe devolver 200 con la lista")
    void getAll_conUsuarioAutenticado_devuelve200() throws Exception {
        when(productoService.findAll()).thenReturn(List.of(RESPONSE));

        mockMvc.perform(get("/productos"))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$[0].nombre").value("Arroz 5kg"))
               .andExpect(jsonPath("$[0].stock").value(150));
    }

    @Test
    @DisplayName("GET /productos sin token debe devolver 401")
    void getAll_sinToken_devuelve401() throws Exception {
        when(jwtTokenProvider.validateToken(any())).thenReturn(false);

        mockMvc.perform(get("/productos"))
               .andExpect(status().isUnauthorized());
    }

    // ---- GET /productos/{id} ----

    @Test
    @WithMockUser
    @DisplayName("GET /productos/{id} con id válido devuelve 200")
    void getById_idValido_devuelve200() throws Exception {
        when(productoService.findById(1L)).thenReturn(RESPONSE);

        mockMvc.perform(get("/productos/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.nombre").value("Arroz 5kg"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /productos/{id} con id inexistente devuelve 404")
    void getById_noExiste_devuelve404() throws Exception {
        when(productoService.findById(99L))
                .thenThrow(new ResourceNotFoundException("Producto no encontrado con id: 99"));

        mockMvc.perform(get("/productos/99"))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.status").value(404))
               .andExpect(jsonPath("$.message").value("Producto no encontrado con id: 99"));
    }

    // ---- POST /productos (requiere ADMIN) ----

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /productos con rol ADMIN y body válido devuelve 200")
    void create_rolAdmin_bodyValido_devuelve200() throws Exception {
        ProductoRequest req = new ProductoRequest("Fideos", "Alimentos", 200, new BigDecimal("1.20"));
        ProductoResponse resp = new ProductoResponse(2L, "Fideos", "Alimentos", 200, new BigDecimal("1.20"));
        when(productoService.create(any())).thenReturn(resp);

        mockMvc.perform(post("/productos").with(csrf())
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(2))
               .andExpect(jsonPath("$.nombre").value("Fideos"));
    }

    @Test
    @WithMockUser(roles = "EMPLEADO")
    @DisplayName("POST /productos con rol EMPLEADO devuelve 403")
    void create_rolEmpleado_devuelve403() throws Exception {
        ProductoRequest req = new ProductoRequest("Fideos", "Alimentos", 200, new BigDecimal("1.20"));

        mockMvc.perform(post("/productos").with(csrf())
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isForbidden());

        verify(productoService, never()).create(any());
    }

    // ---- POST /productos — Validación de campos ----

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /productos con nombre vacío devuelve 400 con detalle")
    void create_nombreVacio_devuelve400() throws Exception {
        ProductoRequest req = new ProductoRequest("", "Alimentos", 100, new BigDecimal("1.00"));

        mockMvc.perform(post("/productos").with(csrf())
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.status").value(400));

        verify(productoService, never()).create(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /productos con stock negativo devuelve 400")
    void create_stockNegativo_devuelve400() throws Exception {
        ProductoRequest req = new ProductoRequest("Válido", "Cat", -5, new BigDecimal("1.00"));

        mockMvc.perform(post("/productos").with(csrf())
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isBadRequest());
    }

    // ---- DELETE /productos/{id} ----

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /productos/{id} con rol ADMIN devuelve 204")
    void delete_rolAdmin_devuelve204() throws Exception {
        doNothing().when(productoService).delete(1L);

        mockMvc.perform(delete("/productos/1").with(csrf()))
               .andExpect(status().isNoContent());

        verify(productoService).delete(1L);
    }

    @Test
    @WithMockUser(roles = "EMPLEADO")
    @DisplayName("DELETE /productos/{id} con rol EMPLEADO devuelve 403")
    void delete_rolEmpleado_devuelve403() throws Exception {
        mockMvc.perform(delete("/productos/1").with(csrf()))
               .andExpect(status().isForbidden());

        verify(productoService, never()).delete(any());
    }
}
