package com.example.demo.repository;

import com.example.demo.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    List<Paciente> findAllByOrderByNombreAsc();
    boolean existsByDni(String dni);
    boolean existsByDniAndIdNot(String dni, Long id);
}
