package com.example.demo.controller;

import com.example.demo.model.Factura;
import com.example.demo.repository.FacturaRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/facturas")
@CrossOrigin(origins = "*")
public class FacturaController {

    private final FacturaRepository repository;

    public FacturaController(FacturaRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Factura> obtenerTodas() { return repository.findAll(); }

    @PostMapping
    public Factura guardar(@RequestBody Factura factura) { return repository.save(factura); }
}