package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "citas")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // CORREGIDO: el cliente envía el objeto Paciente completo bajo la clave
    // "paciente", no un "pacienteId" suelto. Antes este campo no existía
    // aquí, así que Jackson rechazaba el POST por propiedad desconocida.
    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    private String medico;

    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;

    private String motivo;
    private String estado;

    // CORREGIDO: estos dos campos no existían en el servidor. El cliente los
    // envía siempre (aunque hoy en día viajen en 0 porque el modelo Cita del
    // cliente aún no los setea desde ningún constructor — ver nota en el
    // Modelo/Cita.java del cliente).
    @Column(name = "medico_id")
    private Integer medicoId;

    @Column(name = "sala_id")
    private Integer salaId;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    public Cita() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    public String getMedico() { return medico; }
    public void setMedico(String medico) { this.medico = medico; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Integer getMedicoId() { return medicoId; }
    public void setMedicoId(Integer medicoId) { this.medicoId = medicoId; }

    public Integer getSalaId() { return salaId; }
    public void setSalaId(Integer salaId) { this.salaId = salaId; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
}
