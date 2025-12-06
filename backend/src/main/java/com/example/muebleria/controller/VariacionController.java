package com.example.muebleria.controller;

import com.example.muebleria.model.Variacion;
import com.example.muebleria.service.VariacionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/variaciones")
public class VariacionController {

    private final VariacionService variacionService;

    public VariacionController(VariacionService variacionService) {
        this.variacionService = variacionService;
    }

    
    @GetMapping
    public List<Variacion> listarVariaciones() {
        return variacionService.listarVariaciones();
    }

    
    @PostMapping
    public Variacion crearVariacion(@RequestBody Variacion variacion) {
        return variacionService.crearVariacion(variacion);
    }
}