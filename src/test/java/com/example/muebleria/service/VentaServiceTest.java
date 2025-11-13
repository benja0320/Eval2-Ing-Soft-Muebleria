package com.example.muebleria.service;

import com.example.muebleria.exception.StockInsuficienteException;
import com.example.muebleria.model.Cotizacion;
import com.example.muebleria.model.CotizacionDetalle;
import com.example.muebleria.model.Mueble;
import com.example.muebleria.model.enums.EstadoCotizacion;
import com.example.muebleria.model.enums.EstadoMueble;
import com.example.muebleria.model.enums.TamanoMueble;
import com.example.muebleria.repository.CotizacionRepository;
import com.example.muebleria.repository.MuebleRepository;

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
class VentaServiceTest {

    // --- Mocks ---
    @Mock
    private CotizacionRepository cotizacionRepository;

    @Mock
    private MuebleRepository muebleRepository;

    // --- Servicio bajo prueba ---
    @InjectMocks
    private VentaService ventaService;

    // --- Datos de prueba ---
    private Mueble muebleConStock;
    private Cotizacion cotizacionPendiente;
    private CotizacionDetalle detalleVenta;

    @BeforeEach
    void setUp() {
        // 1. Creamos un mueble de prueba con stock 10
        muebleConStock = new Mueble(
                1L, "Silla Gamer", "Silla", new BigDecimal("150000"), 10, // Stock = 10
                EstadoMueble.ACTIVO, TamanoMueble.MEDIANO, "Cuero Sintético"
        );

        // 2. Creamos una cotización pendiente
        cotizacionPendiente = new Cotizacion();
        cotizacionPendiente.setId(1L);
        cotizacionPendiente.setEstado(EstadoCotizacion.PENDIENTE);

        // 3. Creamos un detalle de cotización que pide 2 sillas
        detalleVenta = new CotizacionDetalle();
        detalleVenta.setId(101L);
        detalleVenta.setMueble(muebleConStock);
        detalleVenta.setCantidad(2); // Cantidad pedida = 2
        detalleVenta.setVariacion(null);
        
        // 4. Vinculamos el detalle a la cotización
        cotizacionPendiente.setDetalles(List.of(detalleVenta));
        detalleVenta.setCotizacion(cotizacionPendiente);
    }


    /**
     * Requisito 7: Test de Venta (Caso Exitoso)
     * Verifica que si hay stock, este se descuenta y la cotización se marca como VENDIDA.
     */
    @Test
    void testConfirmarVenta_ConStockSuficiente_DeberiaReducirStockYMarcarComoVendida() {
        // Arrange (Configurar mocks)
        
        // 1. Simular que la cotización SÍ se encuentra
        when(cotizacionRepository.findById(1L)).thenReturn(Optional.of(cotizacionPendiente));
        
        // 2. Simular el guardado del mueble (no es estrictamente necesario, pero es buena práctica)
        when(muebleRepository.save(any(Mueble.class))).thenReturn(muebleConStock);

        // 3. Simular el guardado de la cotización
        when(cotizacionRepository.save(any(Cotizacion.class))).thenReturn(cotizacionPendiente);

        // Act (Ejecutar el método)
        Cotizacion cotizacionVendida = ventaService.confirmarVenta(1L);

        // Assert (Verificar resultados)
        
        // 1. Verificar que el stock del mueble se redujo (10 - 2 = 8)
        assertEquals(8, muebleConStock.getStock());

        // 2. Verificar que el estado de la cotización cambió a VENDIDA
        assertEquals(EstadoCotizacion.VENDIDA, cotizacionVendida.getEstado());

        // 3. Verificar que se llamó a guardar el mueble 1 vez
        verify(muebleRepository, times(1)).save(muebleConStock);
        
        // 4. Verificar que se llamó a guardar la cotización 1 vez
        verify(cotizacionRepository, times(1)).save(cotizacionPendiente);
    }

    /**
     * Requisito 7: Test de Venta (Caso Stock Insuficiente)
     * Verifica que si NO hay stock, se lanza la excepción StockInsuficienteException.
     */
    @Test
    void testConfirmarVenta_ConStockInsuficiente_DeberiaLanzarExcepcion() {
        // Arrange
        
        // 1. Modificar el escenario: El stock es 10, pero pedimos 20
        detalleVenta.setCantidad(20); 

        // 2. Simular que la cotización SÍ se encuentra
        when(cotizacionRepository.findById(1L)).thenReturn(Optional.of(cotizacionPendiente));

        // Act & Assert
        
        // 1. Verificar que el método lanza la excepción correcta
        StockInsuficienteException exception = assertThrows(
                StockInsuficienteException.class,
                () -> {
                    ventaService.confirmarVenta(1L);
                }
        );

        // 2. (Opcional) Verificar el mensaje de error
        assertTrue(exception.getMessage().contains("Stock insuficiente para: Silla Gamer"));

        // 3. ¡MUY IMPORTANTE! Verificar que NADA se guardó (Rollback)
        verify(muebleRepository, never()).save(any(Mueble.class));
        verify(cotizacionRepository, never()).save(any(Cotizacion.class));

        // 4. Verificar que los datos originales no cambiaron
        assertEquals(10, muebleConStock.getStock()); // Sigue siendo 10
        assertEquals(EstadoCotizacion.PENDIENTE, cotizacionPendiente.getEstado()); // Sigue PENDIENTE
    }

    /**
     * Test de borde: Verificar que no se puede vender una cotización ya vendida.
     */
    @Test
    void testConfirmarVenta_CuandoCotizacionYaEstaVendida_DeberiaLanzarExcepcion() {
        // Arrange
        // 1. Marcar la cotización como VENDIDA
        cotizacionPendiente.setEstado(EstadoCotizacion.VENDIDA);

        // 2. Simular que se encuentra
        when(cotizacionRepository.findById(1L)).thenReturn(Optional.of(cotizacionPendiente));

        // Act & Assert
        
        // 1. Verificar que se lanza IllegalStateException
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> {
                    ventaService.confirmarVenta(1L);
                }
        );
        
        assertEquals("La cotización 1 ya fue confirmada como venta.", exception.getMessage());

        // 2. Verificar que NADA se guardó
        verify(muebleRepository, never()).save(any(Mueble.class));
        verify(cotizacionRepository, never()).save(any(Cotizacion.class));
    }
}