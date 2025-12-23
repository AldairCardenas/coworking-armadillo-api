package com.armadillo.coworking.controller;

import com.armadillo.coworking.model.Sala;
import com.armadillo.coworking.repository.SalaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salas")
public class SalaController {

    @Autowired
    private SalaRepository salaRepository;

    // Listar todas las salas o filtrar por capacidad/estado
    @GetMapping
    public List<Sala> listarSalas(
            @RequestParam(required = false) Integer capacidadMinima,
            @RequestParam(required = false) String estado) {

        if (capacidadMinima != null) {
            return salaRepository.findByCapacidadGreaterThanEqual(capacidadMinima);
        }
        if (estado != null) {
            return salaRepository.findByEstado(estado);
        }
        return salaRepository.findAll();
    }

    // Crear sala
    @PostMapping
    public Sala crearSala(@RequestBody Sala sala) {
        return salaRepository.save(sala);
    }

    // Actualizar sala (Sin el campo DNI que causaba error)
    @PutMapping("/{id}")
    public ResponseEntity<Sala> actualizarSala(@PathVariable Long id, @RequestBody Sala salaDetalles) {
        return salaRepository.findById(id)
                .map(sala -> {
                    sala.setNombre(salaDetalles.getNombre());
                    sala.setCapacidad(salaDetalles.getCapacidad());
                    sala.setUbicacion(salaDetalles.getUbicacion());
                    sala.setEquipamiento(salaDetalles.getEquipamiento());
                    sala.setEstado(salaDetalles.getEstado());
                    return ResponseEntity.ok(salaRepository.save(sala));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Eliminar sala (Ahora funcionará en CASCADA con las reservas)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarSala(@PathVariable Long id) {
        return salaRepository.findById(id)
                .map(sala -> {
                    salaRepository.delete(sala);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}