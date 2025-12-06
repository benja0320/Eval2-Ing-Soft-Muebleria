package com.example.muebleria.dto;

import lombok.Data;
import java.util.List;

@Data // para generar geters y setters automáticamente
public class CotizacionRequestDTO {
    // la solicitud solo contiene una lista de los detalles
    private List<CotizacionDetalleDTO> detalles;
}