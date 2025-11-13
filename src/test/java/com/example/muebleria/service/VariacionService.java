package com.example.muebleria.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import com.example.muebleria.model.Variacion;
import com.example.muebleria.repository.VariacionRepository;

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
class VariacionServiceTest {

    @Mock
    private VariacionRepository variacionRepository;

    @InjectMocks
    private VariacionService variacionService;

    private Variacion variacionEjemplo;
    private Variacion nuevaVariacion;

    @BeforeEach
    void setUp() {
        // datos de prueba
        variacionEjemplo = new Variacion(1L, "Barniz Premium", new BigDecimal("15000"));
        
        // una variación que aún no tiene ID
        nuevaVariacion = new Variacion(null, "Ruedas de Goma", new BigDecimal("5000"));
    }

    
    // testea que el servicio puede listar correctamente las variaciones 
    @Test
    void testListarVariaciones() {
        
        when(variacionRepository.findAll()).thenReturn(List.of(variacionEjemplo));

        
        List<Variacion> resultado = variacionService.listarVariaciones();

        
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Barniz Premium", resultado.get(0).getNombre());
        verify(variacionRepository, times(1)).findAll(); // Verificar que se llamó al repositorio
    }

    
    // testea que el servicio puede crear una nueva variación
    @Test
    void testCrearVariacion() {
        
        
        when(variacionRepository.save(any(Variacion.class))).thenAnswer(invocation -> {
            Variacion v = invocation.getArgument(0);
            v.setId(2L); 
            return v;
        });

        
        Variacion guardada = variacionService.crearVariacion(nuevaVariacion);

        
        assertNotNull(guardada);
        assertEquals(2L, guardada.getId()); 
        assertEquals("Ruedas de Goma", guardada.getNombre());
        
        
        verify(variacionRepository, times(1)).save(nuevaVariacion);
    }
}