package com.example.muebleria.patterns.decorator;


// al ser abstracta, no se puede instanciar directamente, 
public abstract class MuebleDecorator implements CalculablePrecio {

    // referencia al componente que se está decorando
    protected CalculablePrecio muebleDecorado;


    // inicializa el decorador con el componente a decorar (añadir nuevo comportamiento)
    public MuebleDecorator(CalculablePrecio muebleDecorado) {
        this.muebleDecorado = muebleDecorado;
    }



     // se usa el patron decorator, llama al metodo calcularPrecio del mueble decorado para obtener el precio base 
    @Override
    public double calcularPrecio() {
        return muebleDecorado.calcularPrecio();
    }
}