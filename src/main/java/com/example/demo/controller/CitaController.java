package com.example.demo.controller;

import com.example.demo.model.Cita;
import com.example.demo.repository.CitaRepository;
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
}