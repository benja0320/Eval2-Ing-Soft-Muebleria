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
        
        // Una variación que aún no tiene ID
        nuevaVariacion = new Variacion(null, "Ruedas de Goma", new BigDecimal("5000"));
    }

    /**
     * Prueba que el servicio lista correctamente todas las variaciones.
     */
    @Test
    void testListarVariaciones() {
        // Arrange (Configurar)
        when(variacionRepository.findAll()).thenReturn(List.of(variacionEjemplo));

        // Act (Actuar)
        List<Variacion> resultado = variacionService.listarVariaciones();

        // Assert (Verificar)
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Barniz Premium", resultado.get(0).getNombre());
        verify(variacionRepository, times(1)).findAll(); // Verificar que se llamó al repositorio
    }

    /**
     * Prueba que el servicio puede crear una nueva variación.
     */
    @Test
    void testCrearVariacion() {
        // Arrange
        // Simular que cuando se guarda, el repositorio le asigna un ID (2L)
        when(variacionRepository.save(any(Variacion.class))).thenAnswer(invocation -> {
            Variacion v = invocation.getArgument(0);
            v.setId(2L); // Simular la asignación de ID
            return v;
        });

        // Act
        Variacion guardada = variacionService.crearVariacion(nuevaVariacion);

        // Assert
        assertNotNull(guardada);
        assertEquals(2L, guardada.getId()); // El ID fue asignado
        assertEquals("Ruedas de Goma", guardada.getNombre());
        
        // Verificar que el repositorio fue llamado una vez
        verify(variacionRepository, times(1)).save(nuevaVariacion);
    }
}