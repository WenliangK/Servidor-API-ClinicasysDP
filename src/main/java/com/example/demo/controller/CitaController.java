package com.example.demo.controller;

import com.example.demo.model.Cita;
import com.example.demo.repository.CitaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
@CrossOrigin(origins = "*")
public class CitaController {

    private final CitaRepository repository;

    public CitaController(CitaRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Cita> obtenerTodas() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cita> obtenerPorId(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // @RequestBody + el modelo Cita corregido (con "paciente", "medicoId",
    // "salaId") es lo que evita el 400 que estabas recibiendo al guardar.
    @PostMapping
    public Cita guardar(@RequestBody Cita cita) {
        return repository.save(cita);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cita> actualizar(@PathVariable Long id, @RequestBody Cita cita) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        cita.setId(id);
        return ResponseEntity.ok(repository.save(cita));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
