package com.example.muebleria.controller;

import com.example.muebleria.dto.CotizacionRequestDTO;
import com.example.muebleria.model.Cotizacion;
import com.example.muebleria.service.CotizacionService;
import com.example.muebleria.service.VentaService;

import jakarta.validation.Valid; // sirve para validar el DTO
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cotizaciones")
@RequiredArgsConstructor
public class CotizacionController {

    private final CotizacionService cotizacionService;
    private final VentaService ventaService;


    //para crear una cotizacion
    @PostMapping
    public ResponseEntity<Cotizacion> crearCotizacion(@Valid @RequestBody CotizacionRequestDTO requestDTO) {
        Cotizacion nuevaCotizacion = cotizacionService.crearCotizacion(requestDTO);
        return new ResponseEntity<>(nuevaCotizacion, HttpStatus.CREATED);
    }

    //para obtener una cotizacion por id
    @GetMapping("/{id}")
    public ResponseEntity<Cotizacion> obtenerCotizacionPorId(@PathVariable Long id) {
        Cotizacion cotizacion = cotizacionService.obtenerCotizacionPorId(id);
        return ResponseEntity.ok(cotizacion);
    }

   
    //sive para confirmar una cotizacion como venta
    @PostMapping("/{id}/confirmar-venta")
    public ResponseEntity<Cotizacion> confirmarVenta(@PathVariable Long id) {
        // la lógica de descontar stock y validar está en VentaService
        Cotizacion cotizacionVendida = ventaService.confirmarVenta(id);
        return ResponseEntity.ok(cotizacionVendida);
    }
}