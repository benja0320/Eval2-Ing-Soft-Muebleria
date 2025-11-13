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

/**
 * Requisito 7: Test para servicio de precios (Patrón Decorator).
 * Valida que los costos de las variaciones se suman correctamente.
 */
class DecoratorPrecioTest {

    private Mueble muebleBase;
    private Variacion variacionBarniz;
    private Variacion variacionCojines;

    @BeforeEach
    void setUp() {
        // 1. Crear un mueble base con precio 100
        muebleBase = new Mueble();
        muebleBase.setNombre("Silla Base");
        muebleBase.setPrecioBase(new BigDecimal("100.00"));

        // 2. Crear una variación de Barniz (costo 20)
        variacionBarniz = new Variacion();
        variacionBarniz.setNombre("Barniz Premium");
        variacionBarniz.setAumentoPrecio(new BigDecimal("20.00"));

        // 3. Crear una variación de Cojines (costo 30)
        variacionCojines = new Variacion();
        variacionCojines.setNombre("Cojines de Seda");
        variacionCojines.setAumentoPrecio(new BigDecimal("30.00"));
    }

    /**
     * Prueba el precio del mueble base sin decoradores.
     */
    @Test
    void testPrecioMuebleBase_SinVariacion() {
        // Arrange
        CalculablePrecio precioFinal = muebleBase;
        
        // Act
        double precioCalculado = precioFinal.calcularPrecio();

        // Assert
        assertEquals(100.0, precioCalculado);
    }

    /**
     * Prueba el precio del mueble con UNA variación.
     */
    @Test
    void testPrecioMueble_ConUnaVariacion() {
        // Arrange
        CalculablePrecio precioFinal = new VariacionDecorator(muebleBase, variacionBarniz);

        // Act
        double precioCalculado = precioFinal.calcularPrecio();

        // Assert
        assertEquals(120.0, precioCalculado); // 100 + 20
    }

    /**
     * Prueba el precio del mueble con MÚLTIPLES variaciones.
     */
    @Test
    void testPrecioMueble_ConMultiplesVariaciones() {
        // Arrange
        CalculablePrecio muebleConBarniz = new VariacionDecorator(muebleBase, variacionBarniz);
        CalculablePrecio precioFinal = new VariacionDecorator(muebleConBarniz, variacionCojines);

        // Act
        double precioCalculado = precioFinal.calcularPrecio();

        // Assert
        assertEquals(150.0, precioCalculado); // 100 + 20 + 30
    }
}