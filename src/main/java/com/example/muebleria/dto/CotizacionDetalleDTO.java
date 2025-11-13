package com.example.muebleria.dto;

import lombok.Data;

@Data // para generar geters y setters automáticamente
public class CotizacionDetalleDTO {
    private Long muebleId;
    private Long variacionId; // sera null si es un mueble "normal"
    private Integer cantidad;
}