package com.reservaespacos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidade Pagamento.
 * Campos conforme especificacao:
 *   id, reserva_id, valor_pago, data_pagamento, metodo_pagamento
 */
@Entity
@Table(name = "pagamentos")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "reserva_id", nullable = false)
    private Reserva reserva;

    @NotNull(message = "Valor pago e obrigatorio")
    @DecimalMin(value = "0.01", message = "Valor minimo e 0.01")
    @Column(name = "valor_pago", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorPago;

    @NotNull(message = "Data de pagamento e obrigatoria")
    @Column(name = "data_pagamento", nullable = false)
    private LocalDate dataPagamento;

    @NotBlank(message = "Metodo de pagamento e obrigatorio")
    @Size(max = 50)
    @Column(name = "metodo_pagamento", nullable = false, length = 50)
    private String metodoPagamento;

    public Pagamento() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Reserva getReserva() { return reserva; }
    public void setReserva(Reserva reserva) { this.reserva = reserva; }
    public BigDecimal getValorPago() { return valorPago; }
    public void setValorPago(BigDecimal valorPago) { this.valorPago = valorPago; }
    public LocalDate getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(LocalDate dataPagamento) { this.dataPagamento = dataPagamento; }
    public String getMetodoPagamento() { return metodoPagamento; }
    public void setMetodoPagamento(String metodoPagamento) { this.metodoPagamento = metodoPagamento; }
}
