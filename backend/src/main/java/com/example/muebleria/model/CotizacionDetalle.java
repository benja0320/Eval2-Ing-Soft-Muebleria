package com.example.muebleria.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cotizacion_detalle")
public class CotizacionDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer cantidad;


    // se encarga de la relacion bidireccional con Cotizacion
    @JsonBackReference 
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "cotizacion_id", nullable = false)
    private Cotizacion cotizacion;



     // se encarga de la relacion bidireccional con Mueble
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "mueble_id", nullable = false)
    private Mueble mueble;


    // se encarga de la relacion bidireccional con Variacion
    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "variacion_id", nullable = true)
    private Variacion variacion;

}