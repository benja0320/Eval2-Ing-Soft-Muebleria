package com.example.muebleria.repository;

import com.example.muebleria.model.Variacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VariacionRepository extends JpaRepository<Variacion, Long> {
}