package com.example.muebleria.dto;

import com.example.muebleria.model.enums.EstadoMueble;
import com.example.muebleria.model.enums.TamanoMueble;

// se usa para las validaciones
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import lombok.Data;
import java.math.BigDecimal;

@Data // para generar geters, setters y mas automaticamente
public class MuebleDTO {

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    private String tipo;

    @NotNull(message = "El precio base es requerido")
    @DecimalMin(value = "0.01", message = "El precio base debe ser positivo")
    private BigDecimal precioBase;

    @NotNull(message = "El stock es requerido")
    @PositiveOrZero(message = "El stock no puede ser negativo")
    private Integer stock;

    @NotNull(message = "El estado es requerido (ACTIVO o INACTIVO)")
    private EstadoMueble estado;

    @NotNull(message = "El tamaño es requerido (GRANDE, MEDIANO o PEQUENO)")
    private TamanoMueble tamano;

    private String material;
}