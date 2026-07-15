package com.example.demo.controller;

import com.example.demo.model.Factura;
import com.example.demo.model.Paciente;
import com.example.demo.repository.FacturaRepository;
import com.example.demo.repository.PacienteRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    private final FacturaRepository repository;
    private final PacienteRepository pacienteRepository;

    public FacturaController(FacturaRepository repository, PacienteRepository pacienteRepository) {
        this.repository = repository;
        this.pacienteRepository = pacienteRepository;
    }

    @GetMapping
    public List<Factura> obtenerTodas() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Factura obtenerPorId(@PathVariable Long id) {
        return buscar(id);
    }

    @PostMapping
    public ResponseEntity<Factura> guardar(@Valid @RequestBody Factura factura) {
        completarPaciente(factura);
        factura.setId(null);
        factura.setFechaEmision(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(factura));
    }

    @PutMapping("/{id}")
    public Factura actualizar(@PathVariable Long id, @Valid @RequestBody Factura entrada) {
        Factura factura = buscar(id);
        completarPaciente(entrada);
        factura.setCitaId(entrada.getCitaId());
        factura.setPacienteId(entrada.getPacienteId());
        factura.setPacienteNombre(entrada.getPacienteNombre());
        factura.setPacienteDni(entrada.getPacienteDni());
        factura.setDescripcion(entrada.getDescripcion());
        factura.setCosto(entrada.getCosto());
        return repository.save(factura);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        repository.delete(buscar(id));
        return ResponseEntity.noContent().build();
    }

    private Factura buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Factura no encontrada"));
    }

    private void completarPaciente(Factura factura) {
        if (factura.getPacienteId() == null) {
            factura.setPacienteNombre(null);
            factura.setPacienteDni(null);
            return;
        }
        Paciente paciente = pacienteRepository.findById(factura.getPacienteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El paciente indicado no existe"));
        factura.setPacienteNombre(paciente.getNombre());
        factura.setPacienteDni(paciente.getDni());
    }
}
