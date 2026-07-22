package com.example.demo.controller;

import com.example.demo.model.Factura;
import com.example.demo.service.FacturaService;
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

import java.util.List;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    private final FacturaService facturaService;

    public FacturaController(FacturaService facturaService) {
        this.facturaService = facturaService;
    }

    @GetMapping
    public List<Factura> obtenerTodas() {
        return facturaService.listar();
    }

    @GetMapping("/{id}")
    public Factura obtenerPorId(@PathVariable Long id) {
        return facturaService.buscar(id);
    }

    @PostMapping
    public ResponseEntity<Factura> guardar(@Valid @RequestBody Factura factura) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(facturaService.crear(factura));
    }

    @PutMapping("/{id}")
    public Factura actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Factura factura
    ) {
        return facturaService.actualizar(id, factura);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        facturaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
