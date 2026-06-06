package com.reservaespacos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

/**
 * Entidade Cliente.
 * Campos conforme especificacao:
 *   id, nome, apelido, telefone, bairro, tipo_documento, numero_documento
 */
@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome e obrigatorio")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nome;

    @NotBlank(message = "Apelido e obrigatorio")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String apelido;

    @NotBlank(message = "Telefone e obrigatorio")
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String telefone;

    @NotBlank(message = "Bairro e obrigatorio")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String bairro;

    @NotBlank(message = "Tipo de documento e obrigatorio")
    @Size(max = 30)
    @Column(name = "tipo_documento", nullable = false, length = 30)
    private String tipoDocumento;

    @NotBlank(message = "Numero de documento e obrigatorio")
    @Size(max = 50)
    @Column(name = "numero_documento", nullable = false, length = 50, unique = true)
    private String numeroDocumento;

    public Cliente() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getApelido() { return apelido; }
    public void setApelido(String apelido) { this.apelido = apelido; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }
    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }
}
