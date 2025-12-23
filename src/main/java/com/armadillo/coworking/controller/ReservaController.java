package com.armadillo.coworking.controller;

import com.armadillo.coworking.model.Reserva;
import com.armadillo.coworking.repository.ReservaRepository;
import com.armadillo.coworking.repository.SalaRepository; // Necesario para búsqueda avanzada
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page; // Para Paginación
import org.springframework.data.domain.PageRequest; // Para Paginación
import org.springframework.data.domain.Pageable; // Para Paginación
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private SalaRepository salaRepository; // Inyectado para búsqueda avanzada de salas

    // 1. Crear reserva con validaciones de negocio
    @PostMapping
    public ResponseEntity<?> crearReserva(@Valid @RequestBody Reserva reserva) {
        String errorValidacion = validarLogicaReserva(reserva, null);
        if (errorValidacion != null) {
            return ResponseEntity.badRequest().body(errorValidacion);
        }

        if (hayChoqueHorario(reserva, null)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("La sala ya está ocupada en ese horario.");
        }

        return ResponseEntity.ok(reservaRepository.save(reserva));
    }

    // 2. Listar reservas (BONO: Paginación añadida)
    // URL: http://localhost:8080/api/reservas?page=0&size=5
    @GetMapping
    public ResponseEntity<?> listarReservas(
            @RequestParam(required = false) Long salaId,
            @RequestParam(required = false) String responsable,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Si hay filtros específicos, usamos la lista tradicional
        if (salaId != null) return ResponseEntity.ok(reservaRepository.findBySalaId(salaId));
        if (responsable != null) return ResponseEntity.ok(reservaRepository.findByNombreResponsableContainingIgnoreCase(responsable));

        // Si no hay filtros, aplicamos Paginación (Punto de Bonificación)
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(reservaRepository.findAll(pageable));
    }

    // 3. Obtener por DNI con mensaje personalizado
    @GetMapping("/dni/{dni}")
    public ResponseEntity<?> obtenerPorDni(@PathVariable String dni) {
        return reservaRepository.findByDniResponsable(dni)
                .<ResponseEntity<?>>map(reserva -> ResponseEntity.ok(reserva))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("El DNI " + dni + " no está registrado en el sistema."));
    }

    // --- PUNTOS OPCIONALES (BONIFICACIÓN COMPLETADOS) ---

    // BONO: Búsqueda avanzada de salas por equipamiento
    // URL: http://localhost:8080/api/reservas/salas/buscar?equipo=Proyector
    @GetMapping("/salas/buscar")
    public ResponseEntity<?> buscarSalasPorEquipo(@RequestParam String equipo) {
        return ResponseEntity.ok(salaRepository.findByEquipamientoContainingIgnoreCase(equipo));
    }

    // BONO: Modificar reserva existente (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<?> modificarReserva(@PathVariable Long id, @Valid @RequestBody Reserva nuevaReserva) {
        return reservaRepository.findById(id).map(reservaExistente -> {
            String error = validarLogicaReserva(nuevaReserva, id);
            if (error != null) return ResponseEntity.badRequest().body(error);

            if (hayChoqueHorario(nuevaReserva, id)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("El nuevo horario choca con otra reserva.");
            }

            reservaExistente.setFechaHoraInicio(nuevaReserva.getFechaHoraInicio());
            reservaExistente.setFechaHoraFin(nuevaReserva.getFechaHoraFin());
            reservaExistente.setSala(nuevaReserva.getSala());
            reservaExistente.setNombreResponsable(nuevaReserva.getNombreResponsable());
            reservaExistente.setCorreoContacto(nuevaReserva.getCorreoContacto());
            reservaExistente.setDniResponsable(nuevaReserva.getDniResponsable());

            return ResponseEntity.ok(reservaRepository.save(reservaExistente));
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reserva no encontrada para modificar."));
    }

    // BONO: Reportes - Estadísticas de uso
    @GetMapping("/reportes/uso-salas")
    public ResponseEntity<Map<String, Long>> obtenerEstadisticasSalas() {
        Map<String, Long> estadisticas = reservaRepository.findAll().stream()
                .collect(Collectors.groupingBy(r -> r.getSala().getNombre(), Collectors.counting()));
        return ResponseEntity.ok(estadisticas);
    }

    // --- MÉTODOS DE APOYO ---

    private String validarLogicaReserva(Reserva r, Long idExcluir) {
        if (!r.getFechaHoraFin().isAfter(r.getFechaHoraInicio())) {
            return "La fecha de fin debe ser posterior a la de inicio.";
        }
        if (Duration.between(r.getFechaHoraInicio(), r.getFechaHoraFin()).toMinutes() < 30) {
            return "La reserva debe tener una duración mínima de 30 minutos.";
        }
        return null;
    }

    private boolean hayChoqueHorario(Reserva r, Long idExcluir) {
        List<Reserva> choques = reservaRepository.findBySalaIdAndFechaHoraInicioLessThanAndFechaHoraFinGreaterThan(
                r.getSala().getId(), r.getFechaHoraFin(), r.getFechaHoraInicio());
        if (idExcluir != null) {
            choques.removeIf(res -> res.getId().equals(idExcluir));
        }
        return !choques.isEmpty();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> manejarValidaciones(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.badRequest().body(mensaje);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> manejarErroresGenerales(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error en el servidor: " + ex.getMessage());
    }
}