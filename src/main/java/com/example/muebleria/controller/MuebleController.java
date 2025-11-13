package com.example.muebleria.controller;

import com.example.muebleria.dto.MuebleDTO;
import com.example.muebleria.model.Mueble;
import com.example.muebleria.service.MuebleService;

import jakarta.validation.Valid; // Importante para activar las validaciones del DTO
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/muebles") // Ruta base para todos los endpoints de este controlador
@RequiredArgsConstructor
public class MuebleController {

    private final MuebleService muebleService;

    /**
     * Requisito 3: Crear un mueble.
     * Recibe un JSON con datos (MuebleDTO) y lo valida.
     * @Valid activa las anotaciones @NotBlank, @NotNull, etc. del DTO.
     */
    @PostMapping
    public ResponseEntity<Mueble> crearMueble(@Valid @RequestBody MuebleDTO muebleDTO) {
        Mueble nuevoMueble = convertirDtoAEntidad(muebleDTO);
        Mueble muebleGuardado = muebleService.crearMueble(nuevoMueble);
        return new ResponseEntity<>(muebleGuardado, HttpStatus.CREATED);
    }

    /**
     * Requisito 3: Listar (leer) todos los muebles (para gestión interna).
     */
    @GetMapping
    public ResponseEntity<List<Mueble>> listarMuebles() {
        List<Mueble> muebles = muebleService.listarMuebles();
        return ResponseEntity.ok(muebles);
    }

    /**
     * Endpoint (Opcional) para listar solo muebles activos (para el público).
     */
    @GetMapping("/activos")
    public ResponseEntity<List<Mueble>> listarMueblesActivos() {
        List<Mueble> muebles = muebleService.listarMueblesActivos();
        return ResponseEntity.ok(muebles);
    }

    /**
     * Obtener un mueble específico por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Mueble> obtenerMueblePorId(@PathVariable Long id) {
        Mueble mueble = muebleService.obtenerMueblePorId(id);
        return ResponseEntity.ok(mueble);
    }

    /**
     * Requisito 3: Actualizar un mueble.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Mueble> actualizarMueble(@PathVariable Long id, @Valid @RequestBody MuebleDTO muebleDTO) {
        Mueble muebleParaActualizar = convertirDtoAEntidad(muebleDTO);
        Mueble muebleActualizado = muebleService.actualizarMueble(id, muebleParaActualizar);
        return ResponseEntity.ok(muebleActualizado);
    }

    /**
     * Requisito 3: Desactivar un mueble (Soft Delete).
     * Usamos PatchMapping porque es una actualización parcial (solo el estado).
     */
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Mueble> desactivarMueble(@PathVariable Long id) {
        Mueble muebleDesactivado = muebleService.desactivarMueble(id);
        return ResponseEntity.ok(muebleDesactivado);
    }


    // --- Método de ayuda ---

    /**
     * Convierte un MuebleDTO (datos de entrada) a una entidad Mueble.
     * Esto separa la lógica de mapeo del controlador.
     */
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