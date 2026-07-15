package com.example.demo.controller;

import com.example.demo.model.Sala;
import com.example.demo.repository.SalaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salas")
@CrossOrigin(origins = "*")
public class SalaController {

    private final SalaRepository repository;

    public SalaController(SalaRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Sala> obtenerTodas() {
        return repository.findAll();
    }

    @PostMapping
    public Sala guardar(@RequestBody Sala sala) {
        return repository.save(sala);
    }

    @DeleteMapping("/{numero}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer numero) {
        if (!repository.existsById(numero)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(numero);
        return ResponseEntity.noContent().build();
    }
}
