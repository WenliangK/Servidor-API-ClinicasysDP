package com.example.demo.controller;

import com.example.demo.dto.EstadoActivoRequest;
import com.example.demo.model.Medico;
import com.example.demo.repository.MedicoRepository;
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
@RequestMapping("/api/medicos")
public class MedicoController {

    private final MedicoRepository repository;

    public MedicoController(MedicoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Medico> obtenerTodos() {
        return repository.findAllByOrderByNombreAsc();
    }

    @GetMapping("/{id}")
    public Medico obtenerPorId(@PathVariable Long id) {
        return buscar(id);
    }

    @PostMapping
    public ResponseEntity<Medico> guardar(@Valid @RequestBody Medico medico) {
        medico.setId(null);
        medico.setActivo(true);
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(medico));
    }

    @PutMapping("/{id}")
    public Medico actualizar(@PathVariable Long id, @Valid @RequestBody Medico entrada) {
        Medico medico = buscar(id);
        medico.setNombre(entrada.getNombre());
        medico.setEspecialidad(entrada.getEspecialidad());
        medico.setTipo(entrada.getTipo());
        medico.setActivo(entrada.isActivo());
        return repository.save(medico);
    }

    @PatchMapping("/{id}/activo")
    public Medico cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoActivoRequest entrada) {
        Medico medico = buscar(id);
        medico.setActivo(entrada.activo());
        return repository.save(medico);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        Medico medico = buscar(id);
        medico.setActivo(false);
        repository.save(medico);
        return ResponseEntity.noContent().build();
    }

    private Medico buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Médico no encontrado"));
    }
}
