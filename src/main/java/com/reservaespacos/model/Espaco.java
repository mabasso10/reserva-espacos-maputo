package com.reservaespacos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "espacos")
public class Espaco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "proprietario_id", nullable = false)
    private Proprietario proprietario;

    @NotBlank(message = "Nome do espaco e obrigatorio")
    @Size(max = 150)
    @Column(name = "nome_espaco", nullable = false, length = 150)
    private String nomeEspaco;

    @Size(max = 2000)
    @Column(length = 2000)
    private String descricao;

    @NotBlank(message = "Bairro e obrigatorio")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String bairro;

    @NotNull(message = "Capacidade e obrigatoria")
    @Min(value = 1, message = "Capacidade minima e 1")
    @Column(nullable = false)
    private Integer capacidade;

    @NotNull(message = "Preco de reserva e obrigatorio")
    @DecimalMin(value = "0.01", message = "Preco minimo e 0.01")
    @Column(name = "preco_reserva", nullable = false, precision = 12, scale = 2)
    private BigDecimal precoReserva;

    @NotBlank(message = "Tipo de evento e obrigatorio")
    @Size(max = 100)
    @Column(name = "tipo_evento", nullable = false, length = 100)
    private String tipoEvento;

    /**
     * Lista de nomes de ficheiros de fotos (máx. 7).
     * Guardada como TEXT JSON na coluna "fotos".
     * Compatível com H2 e PostgreSQL sem extensões adicionais.
     */
    @Convert(converter = StringListConverter.class)
    @Column(name = "fotos", length = 2000)
    private List<String> fotos = new ArrayList<>();

    @Column(nullable = false)
    private Boolean disponibilidade = true;

    public Espaco() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Proprietario getProprietario() { return proprietario; }
    public void setProprietario(Proprietario proprietario) { this.proprietario = proprietario; }
    public String getNomeEspaco() { return nomeEspaco; }
    public void setNomeEspaco(String nomeEspaco) { this.nomeEspaco = nomeEspaco; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }
    public Integer getCapacidade() { return capacidade; }
    public void setCapacidade(Integer capacidade) { this.capacidade = capacidade; }
    public BigDecimal getPrecoReserva() { return precoReserva; }
    public void setPrecoReserva(BigDecimal precoReserva) { this.precoReserva = precoReserva; }
    public String getTipoEvento() { return tipoEvento; }
    public void setTipoEvento(String tipoEvento) { this.tipoEvento = tipoEvento; }
    public List<String> getFotos() { return fotos; }
    public void setFotos(List<String> fotos) { this.fotos = fotos != null ? fotos : new ArrayList<>(); }

    /** Retrocompatibilidade: devolve a primeira foto ou null */
    public String getFoto() {
        return (fotos != null && !fotos.isEmpty()) ? fotos.get(0) : null;
    }

    public Boolean getDisponibilidade() { return disponibilidade; }
    public void setDisponibilidade(Boolean disponibilidade) { this.disponibilidade = disponibilidade; }
}
