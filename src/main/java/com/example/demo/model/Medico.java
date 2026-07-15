package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "medicos")
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del médico es obligatorio")
    @Size(max = 120, message = "El nombre no puede superar 120 caracteres")
    @Column(nullable = false, length = 120)
    private String nombre;

    @NotBlank(message = "La especialidad es obligatoria")
    @Size(max = 100, message = "La especialidad no puede superar 100 caracteres")
    @Column(nullable = false, length = 100)
    private String especialidad;

    @NotBlank(message = "El tipo de atención es obligatorio")
    @Pattern(regexp = "PUBLICO|PRIVADO", message = "El tipo debe ser PUBLICO o PRIVADO")
    @Column(nullable = false, length = 20)
    private String tipo;

    @Column(columnDefinition = "boolean default true")
    private Boolean activo = true;

    public Medico() {
    }

    @PrePersist
    void aplicarValoresIniciales() {
        if (activo == null) {
            activo = true;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = limpiar(nombre);
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = limpiar(especialidad);
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        String valor = limpiar(tipo);
        this.tipo = valor == null ? null : valor.toUpperCase();
    }

    public boolean isActivo() {
        return !Boolean.FALSE.equals(activo);
    }

    public void setActivo(Boolean activo) {
        this.activo = activo == null ? true : activo;
    }

    private static String limpiar(String valor) {
        return valor == null ? null : valor.trim();
    }
}
