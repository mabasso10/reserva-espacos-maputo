package com.reservaespacos.repository;

import com.reservaespacos.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    /** Consultar pagamentos por data */
    List<Pagamento> findByDataPagamento(LocalDate dataPagamento);

    List<Pagamento> findByReservaId(Long reservaId);
}
