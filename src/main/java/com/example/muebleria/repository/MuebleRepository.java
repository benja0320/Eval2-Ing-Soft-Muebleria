package com.example.muebleria.repository;

import com.example.muebleria.model.Mueble;
import com.example.muebleria.model.enums.EstadoMueble;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MuebleRepository extends JpaRepository<Mueble, Long> {


    // busca muebles por su estado (ACTIVO o INACTIVO)
    List<Mueble> findByEstado(EstadoMueble estado);
}