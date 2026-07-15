package com.example.demo.dto;

import com.example.demo.model.Cita;
import jakarta.validation.constraints.NotNull;

public record EstadoCitaRequest(
        @NotNull(message = "El estado de la cita es obligatorio") Cita.Estado estado
) {
}
