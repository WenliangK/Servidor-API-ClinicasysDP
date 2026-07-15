package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "pacientes")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del paciente es obligatorio")
    @Size(max = 120, message = "El nombre no puede superar 120 caracteres")
    @Column(nullable = false, length = 120)
    private String nombre;

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "\\d{8}", message = "El DNI debe contener exactamente 8 dígitos")
    @Column(nullable = false, unique = true, length = 8)
    private String dni;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "9\\d{8}", message = "El teléfono debe contener 9 dígitos y comenzar con 9")
    @Column(nullable = false, length = 9)
    private String telefono;

    @Email(message = "El correo electrónico no tiene un formato válido")
    @Size(max = 150, message = "El correo no puede superar 150 caracteres")
    @Column(length = 150)
    private String email;

    @Column(columnDefinition = "boolean default true")
    private Boolean activo = true;

    public Paciente() {
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

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = limpiar(dni);
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = limpiar(telefono);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        String valor = limpiar(email);
        this.email = valor == null || valor.isEmpty() ? null : valor;
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
