package com.armadillo.coworking.service;

import com.armadillo.coworking.model.Reserva;
import com.armadillo.coworking.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    public Reserva guardarReserva(Reserva nuevaReserva) {
        // 1. Validar duración mínima (30 min)
        long minutos = Duration.between(nuevaReserva.getFechaHoraInicio(), nuevaReserva.getFechaHoraFin()).toMinutes();
        if (minutos < 30) {
            throw new RuntimeException("La reserva debe durar al menos 30 minutos.");
        }

        // 2. Validar que fin sea después de inicio
        if (nuevaReserva.getFechaHoraFin().isBefore(nuevaReserva.getFechaHoraInicio())) {
            throw new RuntimeException("La fecha de fin no puede ser anterior a la de inicio.");
        }

        // 3. Validar superposición (choque de horarios)
        List<Reserva> reservasExistentes = reservaRepository.findBySalaId(nuevaReserva.getSala().getId());
        for (Reserva existente : reservasExistentes) {
            if (nuevaReserva.getFechaHoraInicio().isBefore(existente.getFechaHoraFin()) &&
                    existente.getFechaHoraInicio().isBefore(nuevaReserva.getFechaHoraFin())) {
                throw new RuntimeException("La sala ya está reservada en ese horario.");
            }
        }

        return reservaRepository.save(nuevaReserva);
    }
    public List<Reserva> obtenerTodas() {
        return reservaRepository.findAll();
    }

    public List<Reserva> obtenerPorSala(Long salaId) {
        return reservaRepository.findBySalaId(salaId);
    }
}