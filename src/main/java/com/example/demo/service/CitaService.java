package com.example.demo.service;

import com.example.demo.model.Cita;
import com.example.demo.model.Medico;
import com.example.demo.model.Paciente;
import com.example.demo.repository.CitaRepository;
import com.example.demo.repository.MedicoRepository;
import com.example.demo.repository.PacienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CitaService {

    private static final int PRIMERA_SALA = 1;
    private static final int ULTIMA_SALA = 20;
    private static final long ID_INEXISTENTE = -1L;
    private static final List<Cita.Estado> ESTADOS_FINALES = List.of(
            Cita.Estado.ATENDIDO,
            Cita.Estado.CANCELADO
    );

    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;

    public CitaService(
            CitaRepository citaRepository,
            PacienteRepository pacienteRepository,
            MedicoRepository medicoRepository
    ) {
        this.citaRepository = citaRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
    }

    public List<Cita> listar() {
        return citaRepository.findAllByOrderByFechaHoraDesc();
    }

    public Cita buscar(Long id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cita no encontrada"
                ));
    }

    @Transactional
    public Cita crear(Cita entrada) {
        Cita cita = new Cita();
        cita.setEstado(Cita.Estado.EN_ESPERA);
        copiarDatosEditables(cita, entrada);
        validarConflictos(cita, null);
        cita.setId(null);
        cita.setFechaActualizacion(null);
        return citaRepository.saveAndFlush(cita);
    }

    @Transactional
    public Cita actualizar(Long id, Cita entrada) {
        Cita cita = buscar(id);
        Cita.Estado estadoActual = cita.getEstado();

        copiarDatosEditables(cita, entrada);
        cita.setEstado(estadoActual);

        if (esVigente(estadoActual)) {
            validarConflictos(cita, id);
        }

        return citaRepository.saveAndFlush(cita);
    }

    @Transactional
    public Cita cambiarEstado(Long id, Cita.Estado nuevoEstado) {
        if (nuevoEstado == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El estado de la cita es obligatorio"
            );
        }

        Cita cita = buscar(id);

        if (esVigente(nuevoEstado)) {
            validarConflictos(cita, id);
        }

        cita.setEstado(nuevoEstado);
        return citaRepository.saveAndFlush(cita);
    }

    @Transactional
    public void eliminar(Long id) {
        citaRepository.delete(buscar(id));
        citaRepository.flush();
    }

    private void copiarDatosEditables(Cita destino, Cita entrada) {
        Paciente paciente = resolverPaciente(entrada);
        Medico medico = resolverMedico(entrada);
        LocalDateTime fechaHora = normalizarYValidarFecha(entrada.getFechaHora());
        Integer salaId = validarSala(entrada.getSalaId());

        destino.setPaciente(paciente);
        destino.setMedicoId(medico.getId());
        destino.setMedico(medico.getNombre());
        destino.setFechaHora(fechaHora);
        destino.setSalaId(salaId);
        destino.setMotivo(entrada.getMotivo());
    }

    private Paciente resolverPaciente(Cita entrada) {
        Long pacienteId = entrada.getPaciente() == null
                ? null
                : entrada.getPaciente().getId();

        if (pacienteId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe enviar paciente.id"
            );
        }

        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El paciente indicado no existe"
                ));

        if (!paciente.isActivo()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El paciente está desactivado"
            );
        }

        return paciente;
    }

    private Medico resolverMedico(Cita entrada) {
        Long medicoId = entrada.getMedicoId();

        if (medicoId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe enviar medicoId"
            );
        }

        Medico medico = medicoRepository.findById(medicoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El médico indicado no existe"
                ));

        if (!medico.isActivo()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El médico está desactivado"
            );
        }

        return medico;
    }

    private LocalDateTime normalizarYValidarFecha(LocalDateTime fechaHora) {
        if (fechaHora == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha y hora son obligatorias"
            );
        }

        LocalDateTime normalizada = fechaHora.truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime ahora = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

        if (normalizada.isBefore(ahora)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha de la cita no puede estar en el pasado"
            );
        }

        return normalizada;
    }

    private Integer validarSala(Integer salaId) {
        if (salaId == null || salaId < PRIMERA_SALA || salaId > ULTIMA_SALA) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La sala debe estar entre 1 y 20"
            );
        }
        return salaId;
    }

    private void validarConflictos(Cita cita, Long idActual) {
        long idIgnorado = idActual == null ? ID_INEXISTENTE : idActual;
        LocalDateTime fechaHora = cita.getFechaHora();

        if (citaRepository.contarConflictosMedico(
                cita.getMedicoId(),
                fechaHora,
                ESTADOS_FINALES,
                idIgnorado
        ) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El médico seleccionado ya tiene una cita reservada para esa fecha y hora"
            );
        }

        if (citaRepository.contarConflictosPaciente(
                cita.getPaciente().getId(),
                fechaHora,
                ESTADOS_FINALES,
                idIgnorado
        ) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El paciente ya tiene una cita reservada para esa fecha y hora"
            );
        }

        if (citaRepository.contarConflictosSala(
                cita.getSalaId(),
                fechaHora,
                ESTADOS_FINALES,
                idIgnorado
        ) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La sala seleccionada ya está ocupada para esa fecha y hora"
            );
        }
    }

    private boolean esVigente(Cita.Estado estado) {
        return estado != Cita.Estado.ATENDIDO
                && estado != Cita.Estado.CANCELADO;
    }
}
