package com.example.muebleria.model;

import com.example.muebleria.model.enums.EstadoCotizacion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cotizacion")
public class Cotizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp // asigna la fecha y hora actual automaticamente al crear
    @Column(nullable = false, updatable = false)
    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCotizacion estado = EstadoCotizacion.PENDIENTE;

  

     // se encarga de la relacion bidireccional con CotizacionDetalle
    @JsonManagedReference
    @OneToMany(mappedBy = "cotizacion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CotizacionDetalle> detalles = new ArrayList<>();

    

     // ayuda a agregar un detalle a la cotizacion
    public void addDetalle(CotizacionDetalle detalle) {
        detalles.add(detalle);
        detalle.setCotizacion(this);
    }


    // ayuda a remover un detalle de la cotizacion
    public void removeDetalle(CotizacionDetalle detalle) {
        detalles.remove(detalle);
        detalle.setCotizacion(null);
    }

    
}