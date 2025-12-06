package com.example.muebleria.controller;

import com.example.muebleria.model.Cotizacion;
import com.example.muebleria.service.VentaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ventas")
//@CrossOrigin(origins = "*") 
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @PostMapping("/confirmar/{cotizacionId}")
    public ResponseEntity<?> confirmarVenta(@PathVariable Long cotizacionId) {
        try {
            Cotizacion ventaConfirmada = ventaService.confirmarVenta(cotizacionId);
            return ResponseEntity.ok(ventaConfirmada);
        } catch (Exception e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}