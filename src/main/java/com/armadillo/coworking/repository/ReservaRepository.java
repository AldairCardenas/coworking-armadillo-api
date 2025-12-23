package com.armadillo.coworking.repository;

import com.armadillo.coworking.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    // Filtro por responsable
    List<Reserva> findByNombreResponsableContainingIgnoreCase(String nombre);

    // Filtro por sala
    List<Reserva> findBySalaId(Long salaId);

    // CRUCIAL: Buscar si ya existe una reserva en ese horario para evitar choques
    List<Reserva> findBySalaIdAndFechaHoraInicioLessThanAndFechaHoraFinGreaterThan(
            Long salaId, LocalDateTime fin, LocalDateTime inicio);
    Optional<Reserva> findByDniResponsable(String dni);
}