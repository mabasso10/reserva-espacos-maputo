package com.reservaespacos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class Dtos {

    // ============================================================
    //  Auth — Login
    // ============================================================

    @Schema(description = "Dados necessários para autenticação (POST /auth/login)")
    public static class LoginRequest {

        @Schema(
            description = "Email do utilizador registado no sistema",
            example = "admin@reservas.mz",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Email e obrigatorio")
        @Email(message = "Email invalido")
        public String email;

        @Schema(
            description = "Senha da conta",
            example = "ALTERE_ESTA_SENHA",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Senha e obrigatoria")
        public String senha;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getSenha() { return senha; }
        public void setSenha(String senha) { this.senha = senha; }
    }

    @Schema(description = "Resposta de autenticação com token JWT e dados do utilizador")
    public static class LoginResponse {

        @Schema(
            description = "Token JWT — use no header Authorization: Bearer <token>",
            example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkByZXNlcnZhcy5teiIsImlhdCI6MTcxNzAwMDAwMH0.abc123"
        )
        public String token;

        @Schema(description = "Tipo do token", example = "Bearer")
        public String tipo = "Bearer";

        @Schema(description = "Email do utilizador autenticado", example = "admin@reservas.mz")
        public String email;

        @Schema(description = "Nome do utilizador", example = "Administrador do Sistema")
        public String nome;

        @Schema(description = "Perfil do utilizador: ADMIN | PROPRIETARIO | CLIENTE", example = "ADMIN")
        public String role;

        public LoginResponse(String token, String email, String nome, String role) {
            this.token = token; this.email = email; this.nome = nome; this.role = role;
        }
    }

    // ============================================================
    //  Auth — Registo (público e admin)
    // ============================================================

    @Schema(description = "Dados para criação de nova conta de utilizador")
    public static class RegistoRequest {

        @Schema(description = "Nome completo do utilizador", example = "Armindo Muchanga", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Nome e obrigatorio")
        @Size(max = 100)
        private String nome;

        @Schema(description = "Email único do utilizador", example = "armindo@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Email e obrigatorio")
        @Email(message = "Email invalido")
        private String email;

        @Schema(description = "Senha com mínimo 8 caracteres", example = "senha12345", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Senha e obrigatoria")
        @Size(min = 8, message = "Senha deve ter pelo menos 8 caracteres")
        private String senha;

        @Schema(
            description = "Perfil: CLIENTE | PROPRIETARIO | ADMIN (ADMIN só via /auth/admin/register)",
            example = "CLIENTE",
            allowableValues = {"CLIENTE", "PROPRIETARIO", "ADMIN"}
        )
        private String role = "CLIENTE";

        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getSenha() { return senha; }
        public void setSenha(String senha) { this.senha = senha; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    @Schema(description = "Confirmação de conta criada")
    public static class RegistoResponse {

        @Schema(description = "ID gerado", example = "2")
        public Long id;

        @Schema(description = "Nome do utilizador", example = "Armindo Muchanga")
        public String nome;

        @Schema(description = "Email", example = "armindo@email.com")
        public String email;

        @Schema(description = "Perfil atribuído", example = "CLIENTE")
        public String role;

        public RegistoResponse(Long id, String nome, String email, String role) {
            this.id = id; this.nome = nome; this.email = email; this.role = role;
        }
    }

    // ============================================================
    //  Auth — Alterar senha
    // ============================================================

    @Schema(description = "Dados para alterar a senha do utilizador autenticado")
    public static class AlterarSenhaRequest {

        @Schema(description = "Senha actual do utilizador", example = "ALTERE_ESTA_SENHA", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Senha actual e obrigatoria")
        private String senhaActual;

        @Schema(description = "Nova senha (mínimo 8 caracteres)", example = "NovaSenha2026!", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Nova senha e obrigatoria")
        @Size(min = 8, message = "Nova senha deve ter pelo menos 8 caracteres")
        private String novaSenha;

        public String getSenhaActual() { return senhaActual; }
        public void setSenhaActual(String s) { this.senhaActual = s; }
        public String getNovaSenha() { return novaSenha; }
        public void setNovaSenha(String s) { this.novaSenha = s; }
    }

    // ============================================================
    //  Pagamento — registar (POST /pagamento)
    // ============================================================

    @Schema(description = "Dados para registar um pagamento de reserva")
    public static class PagamentoRequest {

        @Schema(description = "Valor pago em Meticais", example = "450000.00", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Valor pago e obrigatorio")
        @DecimalMin(value = "0.01", message = "Valor minimo e 0.01")
        private java.math.BigDecimal valorPago;

        @Schema(description = "Data do pagamento (yyyy-MM-dd)", example = "2026-06-06", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Data de pagamento e obrigatoria")
        private java.time.LocalDate dataPagamento;

        @Schema(
            description = "Método de pagamento utilizado",
            example = "M-Pesa",
            allowableValues = {"M-Pesa", "e-Mola", "Transferência Bancária", "Numerário"}
        )
        @NotBlank(message = "Metodo de pagamento e obrigatorio")
        @Size(max = 50)
        private String metodoPagamento;

        public java.math.BigDecimal getValorPago() { return valorPago; }
        public void setValorPago(java.math.BigDecimal v) { this.valorPago = v; }
        public java.time.LocalDate getDataPagamento() { return dataPagamento; }
        public void setDataPagamento(java.time.LocalDate v) { this.dataPagamento = v; }
        public String getMetodoPagamento() { return metodoPagamento; }
        public void setMetodoPagamento(String v) { this.metodoPagamento = v; }
    }

    // ============================================================
    //  Reserva — registar (POST /reserva)
    // ============================================================

    @Schema(description = "Dados para registar uma nova reserva")
    public static class ReservaRequest {

        @Schema(description = "Data do evento (yyyy-MM-dd)", example = "2026-07-15", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Data do evento e obrigatoria")
        private java.time.LocalDate dataEvento;

        @Schema(description = "Hora de início do evento (HH:mm)", example = "14:00", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Hora de inicio e obrigatoria")
        private java.time.LocalTime horaInicio;

        @Schema(description = "Hora de fim do evento (HH:mm)", example = "23:00", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Hora de fim e obrigatoria")
        private java.time.LocalTime horaFim;

        @Schema(description = "Número de participantes esperados", example = "250", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Numero de participantes e obrigatorio")
        @Min(value = 1, message = "Minimo 1 participante")
        private Integer numeroParticipantes;

        public java.time.LocalDate getDataEvento() { return dataEvento; }
        public void setDataEvento(java.time.LocalDate v) { this.dataEvento = v; }
        public java.time.LocalTime getHoraInicio() { return horaInicio; }
        public void setHoraInicio(java.time.LocalTime v) { this.horaInicio = v; }
        public java.time.LocalTime getHoraFim() { return horaFim; }
        public void setHoraFim(java.time.LocalTime v) { this.horaFim = v; }
        public Integer getNumeroParticipantes() { return numeroParticipantes; }
        public void setNumeroParticipantes(Integer v) { this.numeroParticipantes = v; }
    }

    // ============================================================
    //  Reserva — actualizar estado
    // ============================================================

    @Schema(description = "Dados para actualizar o estado de uma reserva")
    public static class AtualizarEstadoReservaRequest {

        @Schema(
            description = "Novo estado da reserva",
            example = "CONFIRMADA",
            allowableValues = {"PENDENTE", "CONFIRMADA", "CANCELADA", "CONCLUIDA"},
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Estado e obrigatorio")
        public String estado;

        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }
    }

    // ============================================================
    //  Espaco — criar / actualizar
    // ============================================================

    @Schema(description = "Dados para registar ou actualizar um espaço")
    public static class EspacoRequest {

        @Schema(description = "ID do proprietário do espaço", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "ID do proprietario e obrigatorio")
        public Long proprietarioId;

        @Schema(description = "Nome do espaço", example = "Salão Polana Gold", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Nome do espaco e obrigatorio")
        @Size(max = 150)
        public String nomeEspaco;

        @Schema(description = "Descrição detalhada do espaço", example = "Salão elegante em Polana Cimento com capacidade para 300 pessoas.")
        @Size(max = 2000)
        public String descricao;

        @Schema(description = "Bairro onde o espaço está localizado", example = "Polana Cimento", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Bairro e obrigatorio")
        @Size(max = 100)
        public String bairro;

        @Schema(description = "Capacidade máxima de pessoas", example = "300", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Capacidade e obrigatoria")
        @Min(value = 1, message = "Capacidade minima e 1")
        public Integer capacidade;

        @Schema(description = "Preço de reserva em Meticais", example = "50000.00", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Preco de reserva e obrigatorio")
        @DecimalMin(value = "0.01", message = "Preco minimo e 0.01")
        public BigDecimal precoReserva;

        @Schema(
            description = "Tipo de evento para o qual o espaço é destinado",
            example = "Casamento",
            allowableValues = {"Casamento", "Conferência", "Festa", "Reunião", "Outro"}
        )
        @NotBlank(message = "Tipo de evento e obrigatorio")
        @Size(max = 100)
        public String tipoEvento;

        @Schema(description = "Nome do ficheiro de foto (preenchido via upload)", example = "espaco_1_foto.jpg")
        public String foto;

        @Schema(description = "Indica se o espaço está disponível para reservas", example = "true")
        public Boolean disponibilidade = true;

        public Long getProprietarioId() { return proprietarioId; }
        public void setProprietarioId(Long v) { this.proprietarioId = v; }
        public String getNomeEspaco() { return nomeEspaco; }
        public void setNomeEspaco(String v) { this.nomeEspaco = v; }
        public String getDescricao() { return descricao; }
        public void setDescricao(String v) { this.descricao = v; }
        public String getBairro() { return bairro; }
        public void setBairro(String v) { this.bairro = v; }
        public Integer getCapacidade() { return capacidade; }
        public void setCapacidade(Integer v) { this.capacidade = v; }
        public BigDecimal getPrecoReserva() { return precoReserva; }
        public void setPrecoReserva(BigDecimal v) { this.precoReserva = v; }
        public String getTipoEvento() { return tipoEvento; }
        public void setTipoEvento(String v) { this.tipoEvento = v; }
        public String getFoto() { return foto; }
        public void setFoto(String v) { this.foto = v; }
        public Boolean getDisponibilidade() { return disponibilidade; }
        public void setDisponibilidade(Boolean v) { this.disponibilidade = v; }
    }
}
