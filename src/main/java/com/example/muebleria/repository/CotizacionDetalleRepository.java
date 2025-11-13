package com.example.muebleria.repository;

import com.example.muebleria.model.CotizacionDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CotizacionDetalleRepository extends JpaRepository<CotizacionDetalle, Long> {
    
}