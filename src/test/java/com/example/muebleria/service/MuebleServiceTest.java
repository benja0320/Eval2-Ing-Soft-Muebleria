package com.example.muebleria.service;

import com.example.muebleria.model.Mueble;
import com.example.muebleria.model.enums.EstadoMueble;
import com.example.muebleria.model.enums.TamanoMueble;
import com.example.muebleria.repository.MuebleRepository;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*; 
import static org.mockito.Mockito.*; 

@ExtendWith(MockitoExtension.class) 
class MuebleServiceTest {

    
    @Mock
    private MuebleRepository muebleRepository;

    
    @InjectMocks
    private MuebleService muebleService;

    // datos de prueba
    private Mueble muebleExistente;
    private Mueble muebleNuevo;

    @BeforeEach
    void setUp() {
        
        muebleExistente = new Mueble(
                1L, // id
                "Silla Clásica", // nombre
                "Silla", // tipo
                new BigDecimal("50000.00"), // precioBase
                10, // stock
                EstadoMueble.ACTIVO, // estado
                TamanoMueble.MEDIANO, // tamano
                "Madera" // material
        );

        muebleNuevo = new Mueble(
                null, // Sin id porque es nuevo
                "Mesa Centro",
                "Mesa",
                new BigDecimal("80000.00"),
                5,
                EstadoMueble.ACTIVO, // El servicio lo forzará a ACTIVO
                TamanoMueble.PEQUENO,
                "Vidrio"
        );
    }

    
    // test para gestionar - crear catalogo
    @Test
    void testCrearMueble_DeberiaGuardarYDevolverMueble() {
        
        when(muebleRepository.save(any(Mueble.class))).thenAnswer(invocation -> {
            Mueble m = invocation.getArgument(0);
            m.setId(2L); // simular que la BD le asigna un ID
            return m;
        });

        Mueble muebleGuardado = muebleService.crearMueble(muebleNuevo);

        // se verifica el resultado
        assertNotNull(muebleGuardado);
        assertEquals(2L, muebleGuardado.getId()); 
        assertEquals("Mesa Centro", muebleGuardado.getNombre());
        assertEquals(EstadoMueble.ACTIVO, muebleGuardado.getEstado()); 

        
        verify(muebleRepository, times(1)).save(muebleNuevo);
    }

    
    // test para listar y leer catalogo
    @Test
    void testListarMuebles_DeberiaDevolverLista() {

        when(muebleRepository.findAll()).thenReturn(List.of(muebleExistente));

        List<Mueble> muebles = muebleService.listarMuebles();

        assertNotNull(muebles);
        assertEquals(1, muebles.size());
        assertEquals("Silla Clásica", muebles.get(0).getNombre());
        verify(muebleRepository, times(1)).findAll();
    }

    

     //caso exitoso para obtenr un mueble por id
    @Test
    void testObtenerMueblePorId_CuandoExiste_DeberiaDevolverMueble() {
        when(muebleRepository.findById(1L)).thenReturn(Optional.of(muebleExistente));

        Mueble muebleEncontrado = muebleService.obtenerMueblePorId(1L);

        assertNotNull(muebleEncontrado);
        assertEquals(1L, muebleEncontrado.getId());
        verify(muebleRepository, times(1)).findById(1L);
    }

    
    @Test
    void testObtenerMueblePorId_CuandoNoExiste_DeberiaLanzarExcepcion() {
        when(muebleRepository.findById(99L)).thenReturn(Optional.empty());

        
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            muebleService.obtenerMueblePorId(99L);
        });

        assertEquals("Mueble no encontrado con id: 99", exception.getMessage());
        verify(muebleRepository, times(1)).findById(99L);
    }

    // test para actualizar catalogo
    @Test
    void testActualizarMueble_DeberiaActualizarDatos() {
        
        Mueble datosNuevos = new Mueble();
        datosNuevos.setNombre("Silla Clásica Moderna");
        datosNuevos.setStock(20);

        datosNuevos.setPrecioBase(new BigDecimal("60000"));
        datosNuevos.setTipo(muebleExistente.getTipo());
        datosNuevos.setEstado(muebleExistente.getEstado());
        datosNuevos.setTamano(muebleExistente.getTamano());
        datosNuevos.setMaterial(muebleExistente.getMaterial());


        when(muebleRepository.findById(1L)).thenReturn(Optional.of(muebleExistente));
        
        when(muebleRepository.save(any(Mueble.class))).thenAnswer(i -> i.getArgument(0));

        Mueble muebleActualizado = muebleService.actualizarMueble(1L, datosNuevos);

        
        assertNotNull(muebleActualizado);
        assertEquals(1L, muebleActualizado.getId()); 
        assertEquals("Silla Clásica Moderna", muebleActualizado.getNombre()); 
        assertEquals(20, muebleActualizado.getStock()); 
        assertEquals(new BigDecimal("60000"), muebleActualizado.getPrecioBase());

        verify(muebleRepository, times(1)).findById(1L);
        verify(muebleRepository, times(1)).save(muebleExistente); 
    }

    // test para desactivar (cambiar el estado a inactivo)
    @Test
    void testDesactivarMueble_DeberiaCambiarEstadoAInactivo() {
        
        when(muebleRepository.findById(1L)).thenReturn(Optional.of(muebleExistente));
        when(muebleRepository.save(any(Mueble.class))).thenAnswer(i -> i.getArgument(0));


        Mueble muebleDesactivado = muebleService.desactivarMueble(1L);

        assertNotNull(muebleDesactivado);
        assertEquals(EstadoMueble.INACTIVO, muebleDesactivado.getEstado()); 

        verify(muebleRepository, times(1)).findById(1L);
        verify(muebleRepository, times(1)).save(muebleExistente); 
    }
}