// Cambiamos el paquete para que coincida con la nueva ubicación
package com.example.muebleria.patterns; 

import com.example.muebleria.model.Mueble;
import com.example.muebleria.model.Variacion;
import com.example.muebleria.patterns.decorator.CalculablePrecio;
import com.example.muebleria.patterns.decorator.VariacionDecorator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

 // clase de prueba para el patrón Decorator en el cálculo de precios
class DecoratorPrecioTest {

    private Mueble muebleBase;
    private Variacion variacionBarniz;
    private Variacion variacionCojines;

    @BeforeEach
    void setUp() {
        muebleBase = new Mueble();
        muebleBase.setNombre("Silla Base");
        muebleBase.setPrecioBase(new BigDecimal("100.00"));

        variacionBarniz = new Variacion();
        variacionBarniz.setNombre("Barniz Premium");
        variacionBarniz.setAumentoPrecio(new BigDecimal("20.00"));

        variacionCojines = new Variacion();
        variacionCojines.setNombre("Cojines de Seda");
        variacionCojines.setAumentoPrecio(new BigDecimal("30.00"));
    }

    // test para validar el precio del mueble sin variaciones
    @Test
    void testPrecioMuebleBase_SinVariacion() {
        CalculablePrecio precioFinal = muebleBase;
        
        double precioCalculado = precioFinal.calcularPrecio();

        assertEquals(100.0, precioCalculado);
    }

    // test para validar el precio del mueble con una variación
    @Test
    void testPrecioMueble_ConUnaVariacion() {
        CalculablePrecio precioFinal = new VariacionDecorator(muebleBase, variacionBarniz);

        double precioCalculado = precioFinal.calcularPrecio();

        assertEquals(120.0, precioCalculado); // 100 + 20
    }

    
    // test para validar el precio del mueble con múltiples variaciones
    @Test
    void testPrecioMueble_ConMultiplesVariaciones() {

        CalculablePrecio muebleConBarniz = new VariacionDecorator(muebleBase, variacionBarniz);
        CalculablePrecio precioFinal = new VariacionDecorator(muebleConBarniz, variacionCojines);
        
        double precioCalculado = precioFinal.calcularPrecio();        
        assertEquals(150.0, precioCalculado); // 100 + 20 + 30
    }
}