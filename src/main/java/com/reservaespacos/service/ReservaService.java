package com.reservaespacos.service;

import com.reservaespacos.exception.RecursoNaoEncontradoException;
import com.reservaespacos.exception.RegraDeNegocioException;
import com.reservaespacos.model.Cliente;
import com.reservaespacos.model.Espaco;
import com.reservaespacos.model.Reserva;
import com.reservaespacos.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class ReservaService {

    @Autowired private ReservaRepository reservaRepository;
    @Autowired private ClienteService clienteService;
    @Autowired private EspacoService espacoService;

    public Reserva registar(com.reservaespacos.dto.Dtos.ReservaRequest dto, Long clienteId, Long espacoId) {
        Cliente cliente = clienteService.buscarPorId(clienteId);
        Espaco espaco   = espacoService.buscarPorId(espacoId);

        if (!espaco.getDisponibilidade()) {
            throw new RegraDeNegocioException(
                "O espaco '" + espaco.getNomeEspaco() + "' nao esta disponivel."
            );
        }
        if (dto.getNumeroParticipantes() > espaco.getCapacidade()) {
            throw new RegraDeNegocioException(
                "Numero de participantes (" + dto.getNumeroParticipantes() +
                ") excede a capacidade do espaco (" + espaco.getCapacidade() + ")."
            );
        }
        if (!dto.getHoraFim().isAfter(dto.getHoraInicio())) {
            throw new RegraDeNegocioException("Hora de fim deve ser posterior a hora de inicio.");
        }

        // CORRIGIDO: verificar conflito de horário no mesmo espaço e mesma data
        boolean conflito = reservaRepository.findByEspacoId(espacoId).stream()
            .filter(r -> r.getDataEvento().equals(dto.getDataEvento()))
            .filter(r -> r.getEstado() == Reserva.Estado.PENDENTE
                      || r.getEstado() == Reserva.Estado.CONFIRMADA)
            .anyMatch(r ->
                // sobreposição: novo.inicio < existente.fim && novo.fim > existente.inicio
                dto.getHoraInicio().isBefore(r.getHoraFim()) &&
                dto.getHoraFim().isAfter(r.getHoraInicio())
            );

        if (conflito) {
            throw new RegraDeNegocioException(
                "O espaco '" + espaco.getNomeEspaco() + "' ja tem uma reserva confirmada ou pendente " +
                "para o dia " + dto.getDataEvento() + " nesse intervalo de horário. " +
                "Escolha outro horário ou outro dia."
            );
        }

        long horas = java.time.temporal.ChronoUnit.HOURS.between(dto.getHoraInicio(), dto.getHoraFim());
        if (horas <= 0) horas = 1;
        java.math.BigDecimal valorTotal = espaco.getPrecoReserva().multiply(java.math.BigDecimal.valueOf(horas));

        Reserva reserva = new Reserva();
        reserva.setCliente(cliente);
        reserva.setEspaco(espaco);
        reserva.setDataEvento(dto.getDataEvento());
        reserva.setHoraInicio(dto.getHoraInicio());
        reserva.setHoraFim(dto.getHoraFim());
        reserva.setNumeroParticipantes(dto.getNumeroParticipantes());
        reserva.setValorTotal(valorTotal);
        reserva.setEstado(Reserva.Estado.PENDENTE);

        return reservaRepository.save(reserva);
    }

    public Reserva atualizarEstado(Long id, String novoEstado) {
        Reserva reserva = buscarPorId(id);
        try {
            reserva.setEstado(Reserva.Estado.valueOf(novoEstado.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new RegraDeNegocioException(
                "Estado invalido: " + novoEstado +
                ". Use: PENDENTE, CONFIRMADA, CANCELADA ou CONCLUIDA."
            );
        }
        return reservaRepository.save(reserva);
    }

    @Transactional(readOnly = true)
    public List<Reserva> listarTodas() {
        return reservaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Reserva buscarPorId(Long id) {
        return reservaRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Reserva nao encontrada com ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Reserva> buscarPorDataEvento(LocalDate dataEvento) {
        List<Reserva> lista = reservaRepository.findByDataEvento(dataEvento);
        if (lista.isEmpty()) {
            throw new RecursoNaoEncontradoException(
                "Nenhuma reserva encontrada para a data: " + dataEvento
            );
        }
        return lista;
    }

    @Transactional(readOnly = true)
    public List<Reserva> buscarPorCliente(Long clienteId) {
        clienteService.buscarPorId(clienteId);
        return reservaRepository.findByClienteId(clienteId);
    }

    @Transactional(readOnly = true)
    public List<Reserva> buscarPorEspaco(Long espacoId) {
        espacoService.buscarPorId(espacoId);
        return reservaRepository.findByEspacoId(espacoId);
    }
}
