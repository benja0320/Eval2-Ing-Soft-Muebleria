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

    
    @Mock
    private CotizacionRepository cotizacionRepository;

    @Mock
    private MuebleRepository muebleRepository;

    @InjectMocks
    private VentaService ventaService;

    private Mueble muebleConStock;
    private Cotizacion cotizacionPendiente;
    private CotizacionDetalle detalleVenta;

    @BeforeEach
    void setUp() {
        //  se crea un mueble de prueba con stock 10
        muebleConStock = new Mueble(
                1L, "Silla Gamer", "Silla", new BigDecimal("150000"), 10, 
                EstadoMueble.ACTIVO, TamanoMueble.MEDIANO, "Cuero Sintético"
        );

        // se  crea una cotización pendiente
        cotizacionPendiente = new Cotizacion();
        cotizacionPendiente.setId(1L);
        cotizacionPendiente.setEstado(EstadoCotizacion.PENDIENTE);

        // creamos un detalle de cotización que pide 2 sillas
        detalleVenta = new CotizacionDetalle();
        detalleVenta.setId(101L);
        detalleVenta.setMueble(muebleConStock);
        detalleVenta.setCantidad(2); // cantidad pedida = 2
        detalleVenta.setVariacion(null);
        
        //  se vincula el detalle a la cotización
        cotizacionPendiente.setDetalles(List.of(detalleVenta));
        detalleVenta.setCotizacion(cotizacionPendiente);
    }


    
    //valida el proceso de venta cuando hay stock suficiente
    @Test
    void testConfirmarVenta_ConStockSuficiente_DeberiaReducirStockYMarcarComoVendida() {
        
        when(cotizacionRepository.findById(1L)).thenReturn(Optional.of(cotizacionPendiente));
        when(muebleRepository.save(any(Mueble.class))).thenReturn(muebleConStock);
        when(cotizacionRepository.save(any(Cotizacion.class))).thenReturn(cotizacionPendiente);

        Cotizacion cotizacionVendida = ventaService.confirmarVenta(1L);
        
        assertEquals(8, muebleConStock.getStock());
        assertEquals(EstadoCotizacion.VENDIDA, cotizacionVendida.getEstado());
        verify(muebleRepository, times(1)).save(muebleConStock);
        verify(cotizacionRepository, times(1)).save(cotizacionPendiente);
    }

    // test para validar el proceso de venta cuando no hay stock suficiente
    @Test
    void testConfirmarVenta_ConStockInsuficiente_DeberiaLanzarExcepcion() {
        
        detalleVenta.setCantidad(20); 

        when(cotizacionRepository.findById(1L)).thenReturn(Optional.of(cotizacionPendiente));

        StockInsuficienteException exception = assertThrows(
                StockInsuficienteException.class,
                () -> {
                    ventaService.confirmarVenta(1L);
                }
        );

        assertTrue(exception.getMessage().contains("Stock insuficiente para: Silla Gamer"));

        //  ¡MUY IMPORTANTE! Verificar que NADA se guardó 
        verify(muebleRepository, never()).save(any(Mueble.class));
        verify(cotizacionRepository, never()).save(any(Cotizacion.class));

        // verificar que los datos originales no cambiaron
        assertEquals(10, muebleConStock.getStock()); // Sigue siendo 10
        assertEquals(EstadoCotizacion.PENDIENTE, cotizacionPendiente.getEstado()); // Sigue PENDIENTE
    }

   
    // test para validar que no se puede vender una cotización ya vendida
    @Test
    void testConfirmarVenta_CuandoCotizacionYaEstaVendida_DeberiaLanzarExcepcion() {
        
        cotizacionPendiente.setEstado(EstadoCotizacion.VENDIDA);

        when(cotizacionRepository.findById(1L)).thenReturn(Optional.of(cotizacionPendiente));

        
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> {
                    ventaService.confirmarVenta(1L);
                }
        );
        
        assertEquals("La cotización 1 ya fue confirmada como venta.", exception.getMessage());

        verify(muebleRepository, never()).save(any(Mueble.class));
        verify(cotizacionRepository, never()).save(any(Cotizacion.class));
    }
}