package com.example.demo.controller;

import com.example.demo.model.Medico;
import com.example.demo.repository.MedicoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicos")
@CrossOrigin(origins = "*")
public class MedicoController {

    private final MedicoRepository repository;

    public MedicoController(MedicoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Medico> obtenerTodos() {
        return repository.findAll();
    }
}