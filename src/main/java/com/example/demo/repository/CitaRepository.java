package com.example.demo.repository;

import com.example.demo.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findAllByOrderByFechaHoraDesc();

    @Query("""
            SELECT COUNT(c)
            FROM Cita c
            WHERE c.medicoId = :medicoId
              AND c.fechaHora = :fechaHora
              AND c.estado NOT IN (:estadosFinales)
              AND c.id <> :idIgnorado
            """)
    long contarConflictosMedico(
            @Param("medicoId") Long medicoId,
            @Param("fechaHora") LocalDateTime fechaHora,
            @Param("estadosFinales") Collection<Cita.Estado> estadosFinales,
            @Param("idIgnorado") Long idIgnorado
    );

    @Query("""
            SELECT COUNT(c)
            FROM Cita c
            WHERE c.paciente.id = :pacienteId
              AND c.fechaHora = :fechaHora
              AND c.estado NOT IN (:estadosFinales)
              AND c.id <> :idIgnorado
            """)
    long contarConflictosPaciente(
            @Param("pacienteId") Long pacienteId,
            @Param("fechaHora") LocalDateTime fechaHora,
            @Param("estadosFinales") Collection<Cita.Estado> estadosFinales,
            @Param("idIgnorado") Long idIgnorado
    );

    @Query("""
            SELECT COUNT(c)
            FROM Cita c
            WHERE c.salaId = :salaId
              AND c.fechaHora = :fechaHora
              AND c.estado NOT IN (:estadosFinales)
              AND c.id <> :idIgnorado
            """)
    long contarConflictosSala(
            @Param("salaId") Integer salaId,
            @Param("fechaHora") LocalDateTime fechaHora,
            @Param("estadosFinales") Collection<Cita.Estado> estadosFinales,
            @Param("idIgnorado") Long idIgnorado
    );
}
