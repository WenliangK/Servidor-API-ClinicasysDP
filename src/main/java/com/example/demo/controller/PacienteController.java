package com.example.demo.controller;

import com.example.demo.model.Paciente;
import com.example.demo.repository.PacienteRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
@CrossOrigin(origins = "*")
public class PacienteController {

    private final PacienteRepository repository;

    public PacienteController(PacienteRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Paciente> obtenerTodos() {
        return repository.findAll();
    }
}