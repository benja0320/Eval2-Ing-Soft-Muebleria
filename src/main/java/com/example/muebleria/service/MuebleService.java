package com.example.muebleria.service;

import com.example.muebleria.model.Mueble;
import com.example.muebleria.model.enums.EstadoMueble;
import com.example.muebleria.repository.MuebleRepository;
import jakarta.persistence.EntityNotFoundException; // Buena práctica usar esta excepción
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor // se inyecta MuebleRepository en el constructor
public class MuebleService {

    private final MuebleRepository muebleRepository;


    // crea un mueble nuevo con estado activo
    @Transactional
    public Mueble crearMueble(Mueble mueble) {
        // nos aseguramos que sea un objeto nuevo (sin ID)
        mueble.setId(null); 
        mueble.setEstado(EstadoMueble.ACTIVO); 
        return muebleRepository.save(mueble);
    }


    // lista todos los muebles del catalogo
    @Transactional(readOnly = true) // Transacción de solo lectura, más eficiente
    public List<Mueble> listarMuebles() {
        return muebleRepository.findAll();
    }


    //se usa para obtener un mueble por id
    @Transactional(readOnly = true)
    public Mueble obtenerMueblePorId(Long id) {
        return muebleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mueble no encontrado con id: " + id));
    }



     // para actualizar un mueble existente
    @Transactional
    public Mueble actualizarMueble(Long id, Mueble muebleActualizado) {
        
        Mueble muebleExistente = obtenerMueblePorId(id);

        // se actualizan los campos del objeto existente
        muebleExistente.setNombre(muebleActualizado.getNombre());
        muebleExistente.setTipo(muebleActualizado.getTipo());
        muebleExistente.setPrecioBase(muebleActualizado.getPrecioBase());
        muebleExistente.setStock(muebleActualizado.getStock());
        muebleExistente.setEstado(muebleActualizado.getEstado());
        muebleExistente.setTamano(muebleActualizado.getTamano());
        muebleExistente.setMaterial(muebleActualizado.getMaterial());

        // guarda el objeto actualizado 
        return muebleRepository.save(muebleExistente);
    }

    /**
     * Requisito 3: Desactivar un mueble (Soft Delete).
     * No lo borramos de la BD, solo cambiamos su estado.
     */

     // desactivasmos un mueble existente, no se borra de la BD solo cambiamos su estado
    @Transactional
    public Mueble desactivarMueble(Long id) {
        Mueble muebleExistente = obtenerMueblePorId(id);
        muebleExistente.setEstado(EstadoMueble.INACTIVO);
        return muebleRepository.save(muebleExistente);
    }

    
    // lista todos los muebles activos del catalogo
    @Transactional(readOnly = true)
    public List<Mueble> listarMueblesActivos() {
        return muebleRepository.findByEstado(EstadoMueble.ACTIVO);
        
    }
}