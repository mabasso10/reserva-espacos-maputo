package com.reservaespacos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

/**
 * Utilizador do sistema para autenticacao (Spring Security).
 * Perfis: ADMIN, PROPRIETARIO, CLIENTE
 */
@Entity
@Table(name = "usuarios")
public class Usuario {

    public enum Role { ADMIN, PROPRIETARIO, CLIENTE }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nome;

    @NotBlank
    @Email
    @Column(nullable = false, length = 150, unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.CLIENTE;

    @Column(nullable = false)
    private Boolean activo = true;

    public Usuario() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
