package com.example.demo.controller;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validacion(
            MethodArgumentNotValidException ex
    ) {
        String mensaje = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .distinct()
                .collect(Collectors.joining(". "));

        return respuesta(HttpStatus.BAD_REQUEST, mensaje);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> restriccionValidacion(
            ConstraintViolationException ex
    ) {
        String mensaje = ex.getConstraintViolations()
                .stream()
                .map(violacion -> violacion.getMessage())
                .distinct()
                .collect(Collectors.joining(". "));

        return respuesta(HttpStatus.BAD_REQUEST, mensaje);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> jsonInvalido(
            HttpMessageNotReadableException ex
    ) {
        return respuesta(
                HttpStatus.BAD_REQUEST,
                "El JSON enviado no coincide con el contrato de la API"
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> estadoHttp(
            ResponseStatusException ex
    ) {
        return respuesta(
                HttpStatus.valueOf(ex.getStatusCode().value()),
                ex.getReason()
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> integridad(
            DataIntegrityViolationException ex
    ) {
        String detalle = ex.getMostSpecificCause().getMessage();
        String normalizado = detalle == null
                ? ""
                : detalle.toLowerCase(Locale.ROOT);

        if (normalizado.contains("ux_citas_medico_horario_vigente")) {
            return respuesta(
                    HttpStatus.CONFLICT,
                    "El médico seleccionado ya tiene una cita reservada para esa fecha y hora"
            );
        }

        if (normalizado.contains("ux_citas_paciente_horario_vigente")) {
            return respuesta(
                    HttpStatus.CONFLICT,
                    "El paciente ya tiene una cita reservada para esa fecha y hora"
            );
        }

        if (normalizado.contains("ux_citas_sala_horario_vigente")) {
            return respuesta(
                    HttpStatus.CONFLICT,
                    "La sala seleccionada ya está ocupada para esa fecha y hora"
            );
        }

        if (normalizado.contains("ux_facturas_cita_id")) {
            return respuesta(
                    HttpStatus.CONFLICT,
                    "La cita seleccionada ya cuenta con una factura registrada"
            );
        }

        if (normalizado.contains("ux_pacientes_dni")
                || normalizado.contains("pacientes_dni_key")) {
            return respuesta(
                    HttpStatus.CONFLICT,
                    "Ya existe un paciente con ese DNI"
            );
        }

        return respuesta(
                HttpStatus.CONFLICT,
                "La operación viola una restricción de la base de datos"
        );
    }

    private ResponseEntity<Map<String, Object>> respuesta(
            HttpStatus estado,
            String mensaje
    ) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("timestamp", LocalDateTime.now());
        cuerpo.put("status", estado.value());
        cuerpo.put(
                "error",
                mensaje == null || mensaje.isBlank()
                        ? estado.getReasonPhrase()
                        : mensaje
        );
        return ResponseEntity.status(estado).body(cuerpo);
    }
}
