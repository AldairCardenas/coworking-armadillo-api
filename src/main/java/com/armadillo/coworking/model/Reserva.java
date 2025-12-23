package com.armadillo.coworking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del responsable es obligatorio")
    private String nombreResponsable;

    @NotBlank(message = "El DNI del responsable es obligatorio") // Punto 2
    private String dniResponsable;

    @Email(message = "El formato del correo electrónico no es válido") // Punto 3
    @NotBlank(message = "El correo es obligatorio")
    private String correoContacto;

    @NotNull(message = "La fecha y hora de inicio es obligatoria")
    @Future(message = "No se pueden crear reservas en el pasado") // Punto 3
    private LocalDateTime fechaHoraInicio;

    @NotNull(message = "La fecha y hora de fin es obligatoria")
    private LocalDateTime fechaHoraFin;

    private String proposito;

    @ManyToOne
    @JoinColumn(name = "sala_id", nullable = false)
    private Sala sala;

    // --- GETTERS Y SETTERS ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombreResponsable() { return nombreResponsable; }
    public void setNombreResponsable(String nombreResponsable) { this.nombreResponsable = nombreResponsable; }

    public String getDniResponsable() { return dniResponsable; }
    public void setDniResponsable(String dniResponsable) { this.dniResponsable = dniResponsable; }

    public String getCorreoContacto() { return correoContacto; }
    public void setCorreoContacto(String correoContacto) { this.correoContacto = correoContacto; }

    public LocalDateTime getFechaHoraInicio() { return fechaHoraInicio; }
    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) { this.fechaHoraInicio = fechaHoraInicio; }

    public LocalDateTime getFechaHoraFin() { return fechaHoraFin; }
    public void setFechaHoraFin(LocalDateTime fechaHoraFin) { this.fechaHoraFin = fechaHoraFin; }

    public String getProposito() { return proposito; }
    public void setProposito(String proposito) { this.proposito = proposito; }

    public Sala getSala() { return sala; }
    public void setSala(Sala sala) { this.sala = sala; }
}