package com.example.demo.controller;

import com.example.demo.dto.EstadoActivoRequest;
import com.example.demo.model.Paciente;
import com.example.demo.repository.PacienteRepository;
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
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteRepository repository;

    public PacienteController(PacienteRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Paciente> obtenerTodos() {
        return repository.findAllByOrderByNombreAsc();
    }

    @GetMapping("/{id}")
    public Paciente obtenerPorId(@PathVariable Long id) {
        return buscar(id);
    }

    @PostMapping
    public ResponseEntity<Paciente> guardar(@Valid @RequestBody Paciente paciente) {
        validarDniDisponible(paciente.getDni(), null);
        paciente.setId(null);
        paciente.setActivo(true);
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(paciente));
    }

    @PutMapping("/{id}")
    public Paciente actualizar(@PathVariable Long id, @Valid @RequestBody Paciente entrada) {
        Paciente paciente = buscar(id);
        validarDniDisponible(entrada.getDni(), id);
        paciente.setNombre(entrada.getNombre());
        paciente.setDni(entrada.getDni());
        paciente.setTelefono(entrada.getTelefono());
        paciente.setEmail(entrada.getEmail());
        paciente.setActivo(entrada.isActivo());
        return repository.save(paciente);
    }

    @PatchMapping("/{id}/activo")
    public Paciente cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoActivoRequest entrada) {
        Paciente paciente = buscar(id);
        paciente.setActivo(entrada.activo());
        return repository.save(paciente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        Paciente paciente = buscar(id);
        paciente.setActivo(false);
        repository.save(paciente);
        return ResponseEntity.noContent().build();
    }

    private Paciente buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente no encontrado"));
    }

    private void validarDniDisponible(String dni, Long idActual) {
        boolean ocupado = idActual == null
                ? repository.existsByDni(dni)
                : repository.existsByDniAndIdNot(dni, idActual);
        if (ocupado) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un paciente con ese DNI");
        }
    }
}
