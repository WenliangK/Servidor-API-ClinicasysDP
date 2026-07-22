package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "facturas",
        uniqueConstraints = @UniqueConstraint(
                name = "ux_facturas_cita_id",
                columnNames = "cita_id"
        )
)
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La factura debe estar asociada a una cita")
    @Column(name = "cita_id")
    private Long citaId;

    @NotBlank(message = "La descripción de la factura es obligatoria")
    @Size(max = 1000, message = "La descripción no puede superar 1000 caracteres")
    @Column(nullable = false, length = 1000)
    private String descripcion;

    @NotNull(message = "El costo es obligatorio")
    @DecimalMin(value = "0.01", message = "El costo debe ser mayor que cero")
    @Digits(integer = 10, fraction = 2, message = "El costo debe tener como máximo 2 decimales")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal costo;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDateTime fechaEmision;

    @NotNull(message = "El paciente de la factura es obligatorio")
    @Column(name = "paciente_id")
    private Long pacienteId;

    @Size(max = 120, message = "El nombre del paciente no puede superar 120 caracteres")
    @Column(name = "paciente_nombre", length = 120)
    private String pacienteNombre;

    @Size(max = 8, message = "El DNI no puede superar 8 caracteres")
    @Column(name = "paciente_dni", length = 8)
    private String pacienteDni;

    public Factura() {
    }

    @PrePersist
    void aplicarFechaEmision() {
        if (fechaEmision == null) {
            fechaEmision = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCitaId() {
        return citaId;
    }

    public void setCitaId(Long citaId) {
        this.citaId = citaId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion == null ? null : descripcion.trim();
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }

    public LocalDateTime getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDateTime fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }

    public String getPacienteNombre() {
        return pacienteNombre;
    }

    public void setPacienteNombre(String pacienteNombre) {
        this.pacienteNombre = pacienteNombre == null ? null : pacienteNombre.trim();
    }

    public String getPacienteDni() {
        return pacienteDni;
    }

    public void setPacienteDni(String pacienteDni) {
        this.pacienteDni = pacienteDni == null ? null : pacienteDni.trim();
    }
}
