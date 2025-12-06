package com.example.muebleria.service;

import com.example.muebleria.dto.CotizacionRequestDTO;
import com.example.muebleria.dto.CotizacionDetalleDTO;
import com.example.muebleria.model.Cotizacion;
import com.example.muebleria.model.CotizacionDetalle;
import com.example.muebleria.model.Mueble;
import com.example.muebleria.model.Variacion;
import com.example.muebleria.repository.CotizacionRepository;
import com.example.muebleria.repository.MuebleRepository;
import com.example.muebleria.repository.VariacionRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CotizacionService {

    // se inyectan todos los repositorios necesarios
    private final CotizacionRepository cotizacionRepository;
    private final MuebleRepository muebleRepository;
    private final VariacionRepository variacionRepository;


    // la cotizacion se crea en estado PENDIENTE por defecto
    @Transactional
    public Cotizacion crearCotizacion(CotizacionRequestDTO requestDTO) {

        // crea la entidad principal (cabecera)
        Cotizacion nuevaCotizacion = new Cotizacion();

        // se recorre la lista de detalles DTO
        for (CotizacionDetalleDTO detalleDTO : requestDTO.getDetalles()) {

            // buscar las entidades reales en la BD
            Mueble mueble = muebleRepository.findById(detalleDTO.getMuebleId())
                    .orElseThrow(() -> new EntityNotFoundException("Mueble no encontrado: " + detalleDTO.getMuebleId()));

            Variacion variacion = null;

            if (detalleDTO.getVariacionId() != null) {
                variacion = variacionRepository.findById(detalleDTO.getVariacionId())
                        .orElseThrow(() -> new EntityNotFoundException("Variación no encontrada: " + detalleDTO.getVariacionId()));
            }

            // creamos la entidad  detalle
            CotizacionDetalle detalle = new CotizacionDetalle();
            detalle.setMueble(mueble);
            detalle.setVariacion(variacion); // Será null si no se provee ID
            detalle.setCantidad(detalleDTO.getCantidad());

            // agregar el detalle a la cotización 
            nuevaCotizacion.addDetalle(detalle);
        }

        // guarda automaticamente la cotizacion y sus detalles
        return cotizacionRepository.save(nuevaCotizacion);
    }


    // se usa para obtener una cotizacion por id
    @Transactional(readOnly = true) 
    public Cotizacion obtenerCotizacionPorId(Long id) {
        return cotizacionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cotización no encontrada con id: " + id));
    }
    @Transactional(readOnly = true)
    public List<Cotizacion> listarTodas() { 
        return cotizacionRepository.findAll(); 
    }
}