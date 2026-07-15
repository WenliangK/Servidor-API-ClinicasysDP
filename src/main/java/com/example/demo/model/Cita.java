package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "citas")
public class Cita {

    public enum Estado {
        EN_ESPERA,
        EN_CONSULTORIO,
        ATENDIDO,
        CANCELADO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El paciente es obligatorio")
    @ManyToOne(optional = false)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @Column(nullable = false, length = 120)
    private String medico;

    @NotNull(message = "La fecha y hora son obligatorias")
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @NotBlank(message = "El motivo es obligatorio")
    @Size(max = 500, message = "El motivo no puede superar 500 caracteres")
    @Column(nullable = false, length = 500)
    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Estado estado = Estado.EN_ESPERA;

    @NotNull(message = "El médico es obligatorio")
    @Positive(message = "El identificador del médico debe ser positivo")
    @Column(name = "medico_id", nullable = false)
    private Long medicoId;

    @NotNull(message = "La sala es obligatoria")
    @Positive(message = "El número de sala debe ser positivo")
    @Column(name = "sala_id", nullable = false)
    private Integer salaId;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    public Cita() {
    }

    @PrePersist
    void antesDeGuardar() {
        if (estado == null) {
            estado = Estado.EN_ESPERA;
        }
        fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    void antesDeActualizar() {
        fechaActualizacion = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public String getMedico() {
        return medico;
    }

    public void setMedico(String medico) {
        this.medico = medico == null ? null : medico.trim();
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo == null ? null : motivo.trim();
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Long getMedicoId() {
        return medicoId;
    }

    public void setMedicoId(Long medicoId) {
        this.medicoId = medicoId;
    }

    public Integer getSalaId() {
        return salaId;
    }

    public void setSalaId(Integer salaId) {
        this.salaId = salaId;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
