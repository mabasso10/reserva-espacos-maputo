package com.reservaespacos.service;

import com.reservaespacos.dto.Dtos;
import com.reservaespacos.exception.RecursoNaoEncontradoException;
import com.reservaespacos.exception.RegraDeNegocioException;
import com.reservaespacos.model.Pagamento;
import com.reservaespacos.model.Reserva;
import com.reservaespacos.repository.PagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class PagamentoService {

    @Autowired private PagamentoRepository pagamentoRepository;
    @Autowired private ReservaService reservaService;

    public Pagamento registar(Dtos.PagamentoRequest dto, Long reservaId) {
        Reserva reserva = reservaService.buscarPorId(reservaId);

        if (reserva.getEstado() == Reserva.Estado.CANCELADA) {
            throw new RegraDeNegocioException(
                "Nao e possivel registar pagamento para uma reserva CANCELADA."
            );
        }

        Pagamento pagamento = new Pagamento();
        pagamento.setReserva(reserva);
        pagamento.setValorPago(dto.getValorPago());
        pagamento.setDataPagamento(dto.getDataPagamento());
        pagamento.setMetodoPagamento(dto.getMetodoPagamento());

        if (dto.getValorPago().compareTo(reserva.getValorTotal()) >= 0
                && reserva.getEstado() == Reserva.Estado.PENDENTE) {
            reservaService.atualizarEstado(reservaId, "CONFIRMADA");
        }

        return pagamentoRepository.save(pagamento);
    }

    @Transactional(readOnly = true)
    public List<Pagamento> listarTodos() {
        return pagamentoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Pagamento buscarPorId(Long id) {
        return pagamentoRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException(
                "Pagamento nao encontrado com ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Pagamento> buscarPorData(LocalDate data) {
        List<Pagamento> lista = pagamentoRepository.findByDataPagamento(data);
        if (lista.isEmpty()) {
            throw new RecursoNaoEncontradoException(
                "Nenhum pagamento encontrado na data: " + data);
        }
        return lista;
    }
}
