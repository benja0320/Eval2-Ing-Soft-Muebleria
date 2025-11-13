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

import static org.junit.jupiter.api.Assertions.*; // Importaciones estáticas para Assertions
import static org.mockito.Mockito.*; // Importaciones estáticas para Mockito

@ExtendWith(MockitoExtension.class) // Habilita Mockito para JUnit 5
class MuebleServiceTest {

    // 1. Crear un mock (simulación) del repositorio
    @Mock
    private MuebleRepository muebleRepository;

    // 2. Crear una instancia real del servicio e inyectar los mocks
    @InjectMocks
    private MuebleService muebleService;

    // 3. Datos de prueba
    private Mueble muebleExistente;
    private Mueble muebleNuevo;

    @BeforeEach
    void setUp() {
        // Se ejecuta antes de CADA prueba
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

    /**
     * Requisito 7: Test para Gestión de Catálogo (Crear)
     */
    @Test
    void testCrearMueble_DeberiaGuardarYDevolverMueble() {
        // Arrange (Configurar el mock)
        // Cuando se llame a save() con CUALQUIER Mueble...
        when(muebleRepository.save(any(Mueble.class))).thenAnswer(invocation -> {
            Mueble m = invocation.getArgument(0);
            m.setId(2L); // Simular que la BD le asigna un ID
            return m;
        });

        // Act (Ejecutar el método a probar)
        Mueble muebleGuardado = muebleService.crearMueble(muebleNuevo);

        // Assert (Verificar el resultado)
        assertNotNull(muebleGuardado);
        assertEquals(2L, muebleGuardado.getId()); // Verifica que tenga el ID asignado
        assertEquals("Mesa Centro", muebleGuardado.getNombre());
        assertEquals(EstadoMueble.ACTIVO, muebleGuardado.getEstado()); // Verifica regla de negocio

        // Verificar que el repositorio fue llamado 1 vez
        verify(muebleRepository, times(1)).save(muebleNuevo);
    }

    /**
     * Requisito 7: Test para Gestión de Catálogo (Listar/Leer)
     */
    @Test
    void testListarMuebles_DeberiaDevolverLista() {
        // Arrange
        when(muebleRepository.findAll()).thenReturn(List.of(muebleExistente));

        // Act
        List<Mueble> muebles = muebleService.listarMuebles();

        // Assert
        assertNotNull(muebles);
        assertEquals(1, muebles.size());
        assertEquals("Silla Clásica", muebles.get(0).getNombre());
        verify(muebleRepository, times(1)).findAll();
    }

    /**
     * Test para obtener un mueble por ID (Caso Exitoso)
     */
    @Test
    void testObtenerMueblePorId_CuandoExiste_DeberiaDevolverMueble() {
        // Arrange
        when(muebleRepository.findById(1L)).thenReturn(Optional.of(muebleExistente));

        // Act
        Mueble muebleEncontrado = muebleService.obtenerMueblePorId(1L);

        // Assert
        assertNotNull(muebleEncontrado);
        assertEquals(1L, muebleEncontrado.getId());
        verify(muebleRepository, times(1)).findById(1L);
    }

    /**
     * Test para obtener un mueble por ID (Caso No Encontrado)
     */
    @Test
    void testObtenerMueblePorId_CuandoNoExiste_DeberiaLanzarExcepcion() {
        // Arrange
        when(muebleRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        // Verificamos que se lanza la excepción EntityNotFoundException
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            muebleService.obtenerMueblePorId(99L);
        });

        assertEquals("Mueble no encontrado con id: 99", exception.getMessage());
        verify(muebleRepository, times(1)).findById(99L);
    }

    /**
     * Requisito 7: Test para Gestión de Catálogo (Actualizar)
     */
    @Test
    void testActualizarMueble_DeberiaActualizarDatos() {
        // Arrange
        Mueble datosNuevos = new Mueble();
        datosNuevos.setNombre("Silla Clásica Moderna");
        datosNuevos.setStock(20);
        // ... (se setean todos los campos)
        datosNuevos.setPrecioBase(new BigDecimal("60000"));
        datosNuevos.setTipo(muebleExistente.getTipo());
        datosNuevos.setEstado(muebleExistente.getEstado());
        datosNuevos.setTamano(muebleExistente.getTamano());
        datosNuevos.setMaterial(muebleExistente.getMaterial());


        // 1. Simular la búsqueda del objeto original
        when(muebleRepository.findById(1L)).thenReturn(Optional.of(muebleExistente));
        
        // 2. Simular el guardado (devuelve el objeto que se le pasa)
        when(muebleRepository.save(any(Mueble.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Mueble muebleActualizado = muebleService.actualizarMueble(1L, datosNuevos);

        // Assert
        assertNotNull(muebleActualizado);
        assertEquals(1L, muebleActualizado.getId()); // El ID no cambia
        assertEquals("Silla Clásica Moderna", muebleActualizado.getNombre()); // El nombre sí
        assertEquals(20, muebleActualizado.getStock()); // El stock sí
        assertEquals(new BigDecimal("60000"), muebleActualizado.getPrecioBase());

        verify(muebleRepository, times(1)).findById(1L);
        verify(muebleRepository, times(1)).save(muebleExistente); // Verifica que se guarda el objeto original modificado
    }

    /**
     * Requisito 7: Test para Gestión de Catálogo (Desactivar)
     */
    @Test
    void testDesactivarMueble_DeberiaCambiarEstadoAInactivo() {
        // Arrange
        // 1. Simular la búsqueda
        when(muebleRepository.findById(1L)).thenReturn(Optional.of(muebleExistente));
        // 2. Simular el guardado
        when(muebleRepository.save(any(Mueble.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Mueble muebleDesactivado = muebleService.desactivarMueble(1L);

        // Assert
        assertNotNull(muebleDesactivado);
        assertEquals(EstadoMueble.INACTIVO, muebleDesactivado.getEstado()); // ¡La aserción clave!

        verify(muebleRepository, times(1)).findById(1L);
        verify(muebleRepository, times(1)).save(muebleExistente); // Verifica que se guarda el objeto original con el estado cambiado
    }
}