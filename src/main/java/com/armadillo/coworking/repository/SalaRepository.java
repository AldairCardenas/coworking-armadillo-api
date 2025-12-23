package com.armadillo.coworking.repository;

import com.armadillo.coworking.model.Sala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SalaRepository extends JpaRepository<Sala, Long> {
    List<Sala> findByEstado(String estado);

    List<Sala> findByCapacidadGreaterThanEqual(Integer capacidadMinima);

    List<Sala> findByEquipamientoContainingIgnoreCase(String equipo);
}