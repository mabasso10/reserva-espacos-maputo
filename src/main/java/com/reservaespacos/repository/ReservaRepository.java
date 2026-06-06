package com.reservaespacos.repository;

import com.reservaespacos.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    /** Consultar reservas por data do evento */
    List<Reserva> findByDataEvento(LocalDate dataEvento);

    /** Consultar reservas por cliente */
    List<Reserva> findByClienteId(Long clienteId);

    /** Consultar reservas por espaco */
    List<Reserva> findByEspacoId(Long espacoId);
}
