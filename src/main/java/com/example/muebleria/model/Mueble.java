package com.example.muebleria.model;

// Importaciones actualizadas con la nueva ruta
import com.example.muebleria.model.enums.EstadoMueble;
import com.example.muebleria.model.enums.TamanoMueble;
import com.example.muebleria.patterns.decorator.CalculablePrecio;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal; 

@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Entity
@Table(name = "mueble")
public class Mueble implements CalculablePrecio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String tipo;

    @Column(name = "precio_base", nullable = false)
    private BigDecimal precioBase; // Usamos BigDecimal para precios

    @Column(nullable = false)
    private Integer stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoMueble estado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TamanoMueble tamano;

    private String material;


    // se usa el patron decorator, devuelve el precio base del mueble como un double
    @Override
    public double calcularPrecio() {
        return this.precioBase.doubleValue();
    }
}