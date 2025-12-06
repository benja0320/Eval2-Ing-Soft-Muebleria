package com.example.muebleria.controller;

import com.example.muebleria.dto.MuebleDTO;
import com.example.muebleria.model.Mueble;
import com.example.muebleria.service.MuebleService;

import jakarta.validation.Valid; // se usa para las validaciones de los DTOs
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.PatchMapping; 
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/api/muebles") // ruta base para todos los endpoints de este controlador
@RequiredArgsConstructor
public class MuebleController {

    private final MuebleService muebleService;

    
    @PostMapping
    public ResponseEntity<Mueble> crearMueble(@Valid @RequestBody MuebleDTO muebleDTO) {
        Mueble nuevoMueble = convertirDtoAEntidad(muebleDTO);
        Mueble muebleGuardado = muebleService.crearMueble(nuevoMueble);
        return new ResponseEntity<>(muebleGuardado, HttpStatus.CREATED);
    }


    @GetMapping
    public ResponseEntity<List<Mueble>> listarMuebles() {
        List<Mueble> muebles = muebleService.listarMuebles();
        return ResponseEntity.ok(muebles);
    }

    
    @GetMapping("/activos")
    public ResponseEntity<List<Mueble>> listarMueblesActivos() {
        List<Mueble> muebles = muebleService.listarMueblesActivos();
        return ResponseEntity.ok(muebles);
    }

 
    @GetMapping("/{id}")
    public ResponseEntity<Mueble> obtenerMueblePorId(@PathVariable Long id) {
        Mueble mueble = muebleService.obtenerMueblePorId(id);
        return ResponseEntity.ok(mueble);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Mueble> actualizarMueble(@PathVariable Long id, @Valid @RequestBody MuebleDTO muebleDTO) {
        Mueble muebleParaActualizar = convertirDtoAEntidad(muebleDTO);
        Mueble muebleActualizado = muebleService.actualizarMueble(id, muebleParaActualizar);
        return ResponseEntity.ok(muebleActualizado);
    }


    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Mueble> desactivarMueble(@PathVariable Long id) {
        Mueble muebleDesactivado = muebleService.desactivarMueble(id);
        return ResponseEntity.ok(muebleDesactivado);
    }


     // metodo de apoyo para convertir DTO a entidad
    private Mueble convertirDtoAEntidad(MuebleDTO dto) {
        Mueble mueble = new Mueble();
        mueble.setNombre(dto.getNombre());
        mueble.setTipo(dto.getTipo());
        mueble.setPrecioBase(dto.getPrecioBase());
        mueble.setStock(dto.getStock());
        mueble.setEstado(dto.getEstado());
        mueble.setTamano(dto.getTamano());
        mueble.setMaterial(dto.getMaterial());
        return mueble;
    }
}