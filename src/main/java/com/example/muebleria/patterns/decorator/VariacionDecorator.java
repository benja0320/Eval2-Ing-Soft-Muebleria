package com.example.muebleria.patterns.decorator;

import com.example.muebleria.model.Variacion;
import java.math.BigDecimal;


// añade el costo de una variacion especifica al precio del mueble
public class VariacionDecorator extends MuebleDecorator {

    private final BigDecimal aumentoPrecio;

 

     // se usa el patron decorator, añade el costo de una variacion especifica al precio del mueble
    public VariacionDecorator(CalculablePrecio muebleDecorado, Variacion variacion) {
        // llama al constructor del padre (MuebleDecorator)
        super(muebleDecorado); 
        
        // guarda el costo de esta variación específica
        this.aumentoPrecio = variacion.getAumentoPrecio();
    }


    // sobre escribe el metodo para calcular el precio
    @Override
    public double calcularPrecio() {
        // obtiene el precio base (del objeto envuelto)
        double precioBase = muebleDecorado.calcularPrecio();
        
        // se le suma el aumento de precio de esta variación
        return precioBase + this.aumentoPrecio.doubleValue();
    }
}