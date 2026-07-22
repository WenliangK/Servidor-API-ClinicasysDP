package com.example.demo.controller;

import com.example.demo.dto.EstadoCitaRequest;
import com.example.demo.model.Cita;
import com.example.demo.service.CitaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    @GetMapping
    public List<Cita> obtenerTodas() {
        return citaService.listar();
    }

    @GetMapping("/{id}")
    public Cita obtenerPorId(@PathVariable Long id) {
        return citaService.buscar(id);
    }

    @PostMapping
    public ResponseEntity<Cita> guardar(@Valid @RequestBody Cita cita) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(citaService.crear(cita));
    }

    @PutMapping("/{id}")
    public Cita actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Cita cita
    ) {
        return citaService.actualizar(id, cita);
    }

    @PatchMapping("/{id}/estado")
    public Cita cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody EstadoCitaRequest entrada
    ) {
        return citaService.cambiarEstado(id, entrada.estado());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        citaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
