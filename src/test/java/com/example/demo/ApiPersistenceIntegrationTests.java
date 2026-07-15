package com.example.demo;

import com.example.demo.model.Medico;
import com.example.demo.model.Paciente;
import com.example.demo.repository.CitaRepository;
import com.example.demo.repository.FacturaRepository;
import com.example.demo.repository.MedicoRepository;
import com.example.demo.repository.PacienteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ApiPersistenceIntegrationTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private FacturaRepository facturaRepository;

    @Test
    void creaEditaYDesactivaPacienteConTodosSusDatos() throws Exception {
        mvc.perform(post("/api/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Ana Torres","dni":"12345678","telefono":"912345678",
                                 "email":"ana@correo.pe","activo":false}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.activo").value(true));

        Paciente guardado = pacienteRepository.findAll().getFirst();
        mvc.perform(put("/api/pacientes/{id}", guardado.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Ana Torres Rojas","dni":"12345678","telefono":"998765432",
                                 "email":"ana.rojas@correo.pe","activo":true}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ana Torres Rojas"))
                .andExpect(jsonPath("$.telefono").value("998765432"));

        mvc.perform(patch("/api/pacientes/{id}/activo", guardado.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"activo\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo").value(false));

        assertThat(pacienteRepository.findById(guardado.getId()).orElseThrow().isActivo()).isFalse();
    }

    @Test
    void creaEditaYDesactivaMedico() throws Exception {
        mvc.perform(post("/api/medicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Luis Ramos","especialidad":"Cardiología","tipo":"PRIVADO"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.activo").value(true));

        Medico guardado = medicoRepository.findAll().getFirst();
        mvc.perform(put("/api/medicos/{id}", guardado.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Luis Ramos Díaz","especialidad":"Medicina interna",
                                 "tipo":"PUBLICO","activo":true}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("PUBLICO"));

        mvc.perform(patch("/api/medicos/{id}/activo", guardado.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"activo\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo").value(false));
    }

    @Test
    void guardaCitaResolviendoPacienteYMedicoDesdeLaBaseDeDatos() throws Exception {
        Paciente paciente = nuevoPaciente("87654321");
        Medico medico = nuevoMedico();

        mvc.perform(post("/api/citas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"paciente":{"id":%d},"medico":"Nombre no confiable","medicoId":%d,
                                 "salaId":3,"fechaHora":"2099-05-10T09:30:00","motivo":"Control anual",
                                 "estado":"CANCELADO"}
                                """.formatted(paciente.getId(), medico.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.paciente.id").value(paciente.getId()))
                .andExpect(jsonPath("$.medico").value("Luis Ramos"))
                .andExpect(jsonPath("$.estado").value("EN_ESPERA"));

        assertThat(citaRepository.count()).isEqualTo(1);
    }

    @Test
    void guardaFacturaConDatosAutoritativosDelPaciente() throws Exception {
        Paciente paciente = nuevoPaciente("11223344");

        mvc.perform(post("/api/facturas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"pacienteId":%d,"pacienteNombre":"Nombre alterado","pacienteDni":"00000000",
                                 "descripcion":"Consulta general","costo":50.00}
                                """.formatted(paciente.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pacienteNombre").value("Ana Torres"))
                .andExpect(jsonPath("$.pacienteDni").value("11223344"))
                .andExpect(jsonPath("$.fechaEmision").exists());

        assertThat(facturaRepository.count()).isEqualTo(1);
    }

    @Test
    void rechazaCamposQueNoPertenecenAlContratoJson() throws Exception {
        mvc.perform(post("/api/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Ana Torres","dni":"55667788","telefono":"912345678",
                                 "email":"ana@correo.pe","campoInventado":"no permitido"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El JSON enviado no coincide con el contrato de la API"));

        assertThat(pacienteRepository.count()).isZero();
    }

    private Paciente nuevoPaciente(String dni) {
        Paciente paciente = new Paciente();
        paciente.setNombre("Ana Torres");
        paciente.setDni(dni);
        paciente.setTelefono("912345678");
        paciente.setEmail("ana@correo.pe");
        paciente.setActivo(true);
        return pacienteRepository.save(paciente);
    }

    private Medico nuevoMedico() {
        Medico medico = new Medico();
        medico.setNombre("Luis Ramos");
        medico.setEspecialidad("Cardiología");
        medico.setTipo("PRIVADO");
        medico.setActivo(true);
        return medicoRepository.save(medico);
    }
}
