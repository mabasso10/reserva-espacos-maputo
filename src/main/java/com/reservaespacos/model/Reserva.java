package com.reservaespacos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entidade Reserva.
 * Campos conforme especificacao:
 *   id, cliente_id, data_evento, espaco_id, hora_inicio, hora_fim,
 *   numero_participantes, valor_total, estado
 * Estados: PENDENTE, CONFIRMADA, CANCELADA, CONCLUIDA
 */
@Entity
@Table(name = "reservas")
public class Reserva {

    public enum Estado { PENDENTE, CONFIRMADA, CANCELADA, CONCLUIDA }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @NotNull(message = "Data do evento e obrigatoria")
    @Column(name = "data_evento", nullable = false)
    private LocalDate dataEvento;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "espaco_id", nullable = false)
    private Espaco espaco;

    @NotNull(message = "Hora de inicio e obrigatoria")
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @NotNull(message = "Hora de fim e obrigatoria")
    @Column(name = "hora_fim", nullable = false)
    private LocalTime horaFim;

    @NotNull(message = "Numero de participantes e obrigatorio")
    @Min(value = 1, message = "Minimo 1 participante")
    @Column(name = "numero_participantes", nullable = false)
    private Integer numeroParticipantes;

    @Column(name = "valor_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Estado estado = Estado.PENDENTE;

    public Reserva() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public LocalDate getDataEvento() { return dataEvento; }
    public void setDataEvento(LocalDate dataEvento) { this.dataEvento = dataEvento; }
    public Espaco getEspaco() { return espaco; }
    public void setEspaco(Espaco espaco) { this.espaco = espaco; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public LocalTime getHoraFim() { return horaFim; }
    public void setHoraFim(LocalTime horaFim) { this.horaFim = horaFim; }
    public Integer getNumeroParticipantes() { return numeroParticipantes; }
    public void setNumeroParticipantes(Integer numeroParticipantes) { this.numeroParticipantes = numeroParticipantes; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
}
