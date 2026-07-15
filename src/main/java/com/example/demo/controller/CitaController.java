package com.example.demo.controller;

import com.example.demo.dto.EstadoCitaRequest;
import com.example.demo.model.Cita;
import com.example.demo.model.Medico;
import com.example.demo.model.Paciente;
import com.example.demo.repository.CitaRepository;
import com.example.demo.repository.MedicoRepository;
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

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    private final CitaRepository repository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;

    public CitaController(CitaRepository repository,
                          PacienteRepository pacienteRepository,
                          MedicoRepository medicoRepository) {
        this.repository = repository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
    }

    @GetMapping
    public List<Cita> obtenerTodas() {
        return repository.findAllByOrderByFechaHoraDesc();
    }

    @GetMapping("/{id}")
    public Cita obtenerPorId(@PathVariable Long id) {
        return buscar(id);
    }

    @PostMapping
    public ResponseEntity<Cita> guardar(@Valid @RequestBody Cita cita) {
        validarFecha(cita.getFechaHora());
        resolverRelaciones(cita);
        cita.setId(null);
        cita.setEstado(Cita.Estado.EN_ESPERA);
        cita.setFechaActualizacion(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(cita));
    }

    @PutMapping("/{id}")
    public Cita actualizar(@PathVariable Long id, @Valid @RequestBody Cita entrada) {
        Cita cita = buscar(id);
        validarFecha(entrada.getFechaHora());
        resolverRelaciones(entrada);
        cita.setPaciente(entrada.getPaciente());
        cita.setMedicoId(entrada.getMedicoId());
        cita.setMedico(entrada.getMedico());
        cita.setSalaId(entrada.getSalaId());
        cita.setFechaHora(entrada.getFechaHora());
        cita.setMotivo(entrada.getMotivo());
        if (entrada.getEstado() != null) {
            cita.setEstado(entrada.getEstado());
        }
        return repository.save(cita);
    }

    @PatchMapping("/{id}/estado")
    public Cita cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoCitaRequest entrada) {
        Cita cita = buscar(id);
        cita.setEstado(entrada.estado());
        return repository.save(cita);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        Cita cita = buscar(id);
        repository.delete(cita);
        return ResponseEntity.noContent().build();
    }

    private Cita buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cita no encontrada"));
    }

    private void resolverRelaciones(Cita cita) {
        Long pacienteId = cita.getPaciente() == null ? null : cita.getPaciente().getId();
        if (pacienteId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe enviar paciente.id");
        }
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El paciente indicado no existe"));
        if (!paciente.isActivo()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El paciente está desactivado");
        }

        if (cita.getMedicoId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe enviar medicoId");
        }
        Medico medico = medicoRepository.findById(cita.getMedicoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El médico indicado no existe"));
        if (!medico.isActivo()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El médico está desactivado");
        }

        cita.setPaciente(paciente);
        cita.setMedico(medico.getNombre());
    }

    private void validarFecha(LocalDateTime fechaHora) {
        if (fechaHora != null && fechaHora.isBefore(LocalDateTime.now().minusMinutes(1))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de la cita no puede estar en el pasado");
        }
    }
}
