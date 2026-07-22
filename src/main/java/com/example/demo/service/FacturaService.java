package com.example.demo.service;

import com.example.demo.model.Cita;
import com.example.demo.model.Factura;
import com.example.demo.model.Paciente;
import com.example.demo.repository.CitaRepository;
import com.example.demo.repository.FacturaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final CitaRepository citaRepository;

    public FacturaService(
            FacturaRepository facturaRepository,
            CitaRepository citaRepository
    ) {
        this.facturaRepository = facturaRepository;
        this.citaRepository = citaRepository;
    }

    public List<Factura> listar() {
        return facturaRepository.findAllByOrderByFechaEmisionDesc();
    }

    public Factura buscar(Long id) {
        return facturaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Factura no encontrada"
                ));
    }

    @Transactional
    public Factura crear(Factura entrada) {
        Factura factura = new Factura();
        copiarYValidar(factura, entrada, null);
        factura.setId(null);
        factura.setFechaEmision(null);
        return facturaRepository.saveAndFlush(factura);
    }

    @Transactional
    public Factura actualizar(Long id, Factura entrada) {
        Factura factura = buscar(id);
        copiarYValidar(factura, entrada, id);
        return facturaRepository.saveAndFlush(factura);
    }

    @Transactional
    public void eliminar(Long id) {
        facturaRepository.delete(buscar(id));
        facturaRepository.flush();
    }

    private void copiarYValidar(
            Factura destino,
            Factura entrada,
            Long idActual
    ) {
        if (entrada.getCitaId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La factura debe estar asociada a una cita"
            );
        }

        Cita cita = citaRepository.findById(entrada.getCitaId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "La cita indicada no existe"
                ));

        if (cita.getEstado() != Cita.Estado.ATENDIDO) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Solo se puede facturar una cita con estado ATENDIDO"
            );
        }

        Paciente paciente = cita.getPaciente();
        if (paciente == null || paciente.getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La cita no tiene un paciente válido asociado"
            );
        }

        if (entrada.getPacienteId() != null
                && !Objects.equals(entrada.getPacienteId(), paciente.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El paciente de la factura no coincide con el paciente de la cita"
            );
        }

        boolean duplicada = idActual == null
                ? facturaRepository.existsByCitaId(entrada.getCitaId())
                : facturaRepository.existsByCitaIdAndIdNot(entrada.getCitaId(), idActual);

        if (duplicada) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La cita seleccionada ya cuenta con una factura registrada"
            );
        }

        destino.setCitaId(cita.getId());
        destino.setPacienteId(paciente.getId());
        destino.setPacienteNombre(paciente.getNombre());
        destino.setPacienteDni(paciente.getDni());
        destino.setDescripcion(entrada.getDescripcion());
        destino.setCosto(entrada.getCosto());
    }
}
