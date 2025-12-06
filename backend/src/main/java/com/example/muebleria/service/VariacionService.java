package com.example.muebleria.service;

import com.example.muebleria.model.Variacion;
import com.example.muebleria.repository.VariacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VariacionService {

    private final VariacionRepository variacionRepository;


    // lista todas las variaciones disponibles en la BD
    @Transactional(readOnly = true)
    public List<Variacion> listarVariaciones() {
        return variacionRepository.findAll();
    }



     // crea una nueva variante
    @Transactional
    public Variacion crearVariacion(Variacion variacion) {
        variacion.setId(null); // Asegurar que es nueva
        return variacionRepository.save(variacion);
    }
}