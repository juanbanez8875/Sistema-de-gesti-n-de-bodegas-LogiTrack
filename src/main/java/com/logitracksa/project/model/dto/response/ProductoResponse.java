package com.logitracksa.project.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class ProductoResponse {

    private Long id;
    private String nombre;
    private String categoria;
    private Integer stock;
    private BigDecimal precio;
}
