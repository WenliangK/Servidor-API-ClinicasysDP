package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "salas")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Sala {

    // El cliente usa "numero" como identificador de negocio (no un "id"
    // autogenerado), así que respetamos ese mismo campo como llave primaria.
    @Id
    private Integer numero;

    private String descripcion;
    private boolean disponible;

    public Sala() {}

    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
}
