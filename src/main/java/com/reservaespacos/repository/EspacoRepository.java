package com.reservaespacos.repository;

import com.reservaespacos.model.Espaco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EspacoRepository extends JpaRepository<Espaco, Long> {

    /** Consultar espacos por proprietario */
    List<Espaco> findByProprietarioId(Long proprietarioId);

    /** Consultar espacos por tipo de evento - pesquisa parcial, case-insensitive */
    List<Espaco> findByTipoEventoContainingIgnoreCase(String tipoEvento);

    /** Espacos disponiveis */
    List<Espaco> findByDisponibilidadeTrue();
}
