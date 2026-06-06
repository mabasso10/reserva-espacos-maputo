package com.reservaespacos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

/**
 * Entidade Proprietario.
 * Campos conforme especificacao:
 *   id, nome, apelido, telefone, bairro, nuit
 */
@Entity
@Table(name = "proprietarios")
public class Proprietario {

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

    @NotBlank(message = "NUIT e obrigatorio")
    @Size(max = 30)
    @Column(nullable = false, length = 30, unique = true)
    private String nuit;

    public Proprietario() {}

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
    public String getNuit() { return nuit; }
    public void setNuit(String nuit) { this.nuit = nuit; }
}
