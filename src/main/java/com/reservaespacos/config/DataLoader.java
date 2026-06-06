package com.reservaespacos.config;

import com.reservaespacos.model.*;
import com.reservaespacos.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Carrega dados iniciais na BD ao iniciar a aplicação pela primeira vez.
 * Só executa se a tabela de utilizadores estiver vazia.
 *
 * CREDENCIAIS INICIAIS:
 *   admin@reservas.mz / ALTERE_ESTA_SENHA
 *
 * IMPORTANTE: Altere a senha do admin imediatamente após o primeiro login.
 */
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired private UsuarioRepository usuarioRepo;
    @Autowired private ClienteRepository clienteRepo;
    @Autowired private ProprietarioRepository proprietarioRepo;
    @Autowired private EspacoRepository espacoRepo;
    @Autowired private ReservaRepository reservaRepo;
    @Autowired private PagamentoRepository pagamentoRepo;
    @Autowired private PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        if (usuarioRepo.count() > 0) return; // Evita duplicados em cada reinício

        // ================================================================
        // UTILIZADOR ADMINISTRADOR - ÚNICO CRIADO AUTOMATICAMENTE
        // Altere a senha após o primeiro login
        // ================================================================
        Usuario admin = new Usuario();
        admin.setNome("Administrador do Sistema");
        admin.setEmail("admin@reservas.mz");
        admin.setSenha(encoder.encode("ALTERE_ESTA_SENHA"));
        admin.setRole(Usuario.Role.ADMIN);
        usuarioRepo.save(admin);

        // ================================================================
        // CLIENTES - dados reais de Maputo
        // ================================================================
        Cliente c1 = new Cliente();
        c1.setNome("Armindo"); c1.setApelido("Muchanga");
        c1.setTelefone("+258 84 312 4567"); c1.setBairro("Polana Cimento");
        c1.setTipoDocumento("BI"); c1.setNumeroDocumento("1234567890MZ");
        clienteRepo.save(c1);

        Cliente c2 = new Cliente();
        c2.setNome("Esperança"); c2.setApelido("Guambe");
        c2.setTelefone("+258 82 456 7890"); c2.setBairro("Sommerschield");
        c2.setTipoDocumento("BI"); c2.setNumeroDocumento("0987654321MZ");
        clienteRepo.save(c2);

        Cliente c3 = new Cliente();
        c3.setNome("Hélder"); c3.setApelido("Sithole");
        c3.setTelefone("+258 86 789 0123"); c3.setBairro("Malhangalene");
        c3.setTipoDocumento("Passaporte"); c3.setNumeroDocumento("MZ0023456A");
        clienteRepo.save(c3);

        Cliente c4 = new Cliente();
        c4.setNome("Lurdes"); c4.setApelido("Nhaca");
        c4.setTelefone("+258 84 234 5678"); c4.setBairro("Maxaquene");
        c4.setTipoDocumento("BI"); c4.setNumeroDocumento("1122334455MZ");
        clienteRepo.save(c4);

        Cliente c5 = new Cliente();
        c5.setNome("Osvaldo"); c5.setApelido("Cossa");
        c5.setTelefone("+258 82 345 6789"); c5.setBairro("Julius Nyerere");
        c5.setTipoDocumento("BI"); c5.setNumeroDocumento("6677889900MZ");
        clienteRepo.save(c5);

        // ================================================================
        // PROPRIETÁRIOS
        // ================================================================
        Proprietario p1 = new Proprietario();
        p1.setNome("Joaquim"); p1.setApelido("Nhamithambo");
        p1.setTelefone("+258 84 678 9012"); p1.setBairro("Polana Cimento");
        p1.setNuit("400112233");
        proprietarioRepo.save(p1);

        Proprietario p2 = new Proprietario();
        p2.setNome("Celeste"); p2.setApelido("Tivane");
        p2.setTelefone("+258 82 789 0124"); p2.setBairro("Julius Nyerere");
        p2.setNuit("400998877");
        proprietarioRepo.save(p2);

        // ================================================================
        // ESPAÇOS - locais reais de eventos em Maputo
        // ================================================================
        Espaco e1 = new Espaco();
        e1.setProprietario(p1);
        e1.setNomeEspaco("Salão Polana Gold");
        e1.setDescricao("Salão elegante em Polana Cimento com capacidade para 300 pessoas, ar condicionado central, sistema de som profissional e catering disponível.");
        e1.setBairro("Polana Cimento"); e1.setCapacidade(300);
        e1.setPrecoReserva(new BigDecimal("50000.00"));
        e1.setTipoEvento("Casamento"); e1.setDisponibilidade(true);
        espacoRepo.save(e1);

        Espaco e2 = new Espaco();
        e2.setProprietario(p1);
        e2.setNomeEspaco("Centro de Conferências Sommerschield");
        e2.setDescricao("Sala de conferências moderna com projector HD, wi-fi de alta velocidade, ar condicionado e capacidade para 150 pessoas.");
        e2.setBairro("Sommerschield"); e2.setCapacidade(150);
        e2.setPrecoReserva(new BigDecimal("30000.00"));
        e2.setTipoEvento("Conferência"); e2.setDisponibilidade(true);
        espacoRepo.save(e2);

        Espaco e3 = new Espaco();
        e3.setProprietario(p2);
        e3.setNomeEspaco("Jardim dos Eventos Malhangalene");
        e3.setDescricao("Espaço ao ar livre com jardim tropical e iluminação ambiente, ideal para festas e eventos sociais até 500 pessoas.");
        e3.setBairro("Malhangalene"); e3.setCapacidade(500);
        e3.setPrecoReserva(new BigDecimal("75000.00"));
        e3.setTipoEvento("Festa"); e3.setDisponibilidade(true);
        espacoRepo.save(e3);

        Espaco e4 = new Espaco();
        e4.setProprietario(p2);
        e4.setNomeEspaco("Auditório Julius Nyerere");
        e4.setDescricao("Auditório com palco profissional, iluminação cénica, sistema de som surround e capacidade para 400 pessoas.");
        e4.setBairro("Julius Nyerere"); e4.setCapacidade(400);
        e4.setPrecoReserva(new BigDecimal("60000.00"));
        e4.setTipoEvento("Conferência"); e4.setDisponibilidade(false);
        espacoRepo.save(e4);

        Espaco e5 = new Espaco();
        e5.setProprietario(p1);
        e5.setNomeEspaco("Sala VIP Executive Polana");
        e5.setDescricao("Sala executiva climatizada para reuniões e eventos corporativos de pequena dimensão, com equipamento audiovisual completo.");
        e5.setBairro("Polana Cimento"); e5.setCapacidade(50);
        e5.setPrecoReserva(new BigDecimal("15000.00"));
        e5.setTipoEvento("Reunião"); e5.setDisponibilidade(true);
        espacoRepo.save(e5);

        // ================================================================
        // RESERVAS
        // ================================================================
        Reserva r1 = new Reserva();
        r1.setCliente(c1); r1.setEspaco(e1);
        r1.setDataEvento(LocalDate.now().plusDays(10));
        r1.setHoraInicio(LocalTime.of(14, 0));
        r1.setHoraFim(LocalTime.of(23, 0));
        r1.setNumeroParticipantes(250);
        r1.setValorTotal(new BigDecimal("450000.00"));
        r1.setEstado(Reserva.Estado.CONFIRMADA);
        reservaRepo.save(r1);

        Reserva r2 = new Reserva();
        r2.setCliente(c2); r2.setEspaco(e2);
        r2.setDataEvento(LocalDate.now().plusDays(5));
        r2.setHoraInicio(LocalTime.of(9, 0));
        r2.setHoraFim(LocalTime.of(17, 0));
        r2.setNumeroParticipantes(100);
        r2.setValorTotal(new BigDecimal("240000.00"));
        r2.setEstado(Reserva.Estado.PENDENTE);
        reservaRepo.save(r2);

        Reserva r3 = new Reserva();
        r3.setCliente(c3); r3.setEspaco(e3);
        r3.setDataEvento(LocalDate.now().plusDays(20));
        r3.setHoraInicio(LocalTime.of(18, 0));
        r3.setHoraFim(LocalTime.of(23, 0));
        r3.setNumeroParticipantes(400);
        r3.setValorTotal(new BigDecimal("375000.00"));
        r3.setEstado(Reserva.Estado.PENDENTE);
        reservaRepo.save(r3);

        Reserva r4 = new Reserva();
        r4.setCliente(c4); r4.setEspaco(e5);
        r4.setDataEvento(LocalDate.now().plusDays(3));
        r4.setHoraInicio(LocalTime.of(10, 0));
        r4.setHoraFim(LocalTime.of(13, 0));
        r4.setNumeroParticipantes(30);
        r4.setValorTotal(new BigDecimal("45000.00"));
        r4.setEstado(Reserva.Estado.CONFIRMADA);
        reservaRepo.save(r4);

        Reserva r5 = new Reserva();
        r5.setCliente(c5); r5.setEspaco(e1);
        r5.setDataEvento(LocalDate.now().plusDays(15));
        r5.setHoraInicio(LocalTime.of(15, 0));
        r5.setHoraFim(LocalTime.of(22, 0));
        r5.setNumeroParticipantes(200);
        r5.setValorTotal(new BigDecimal("350000.00"));
        r5.setEstado(Reserva.Estado.CANCELADA);
        reservaRepo.save(r5);

        // ================================================================
        // PAGAMENTOS
        // ================================================================
        Pagamento pg1 = new Pagamento();
        pg1.setReserva(r1);
        pg1.setValorPago(new BigDecimal("450000.00"));
        pg1.setDataPagamento(LocalDate.now().minusDays(2));
        pg1.setMetodoPagamento("M-Pesa");
        pagamentoRepo.save(pg1);

        Pagamento pg2 = new Pagamento();
        pg2.setReserva(r2);
        pg2.setValorPago(new BigDecimal("120000.00")); // Entrada parcial
        pg2.setDataPagamento(LocalDate.now().minusDays(1));
        pg2.setMetodoPagamento("Transferência Bancária");
        pagamentoRepo.save(pg2);

        Pagamento pg3 = new Pagamento();
        pg3.setReserva(r4);
        pg3.setValorPago(new BigDecimal("45000.00"));
        pg3.setDataPagamento(LocalDate.now());
        pg3.setMetodoPagamento("e-Mola");
        pagamentoRepo.save(pg3);

        System.out.println("============================================================");
        System.out.println("  Dados iniciais carregados com sucesso!");
        System.out.println("  ADMIN: admin@reservas.mz / ALTERE_ESTA_SENHA");
        System.out.println("  IMPORTANTE: Altere a senha imediatamente após o login!");
        System.out.println("============================================================");
    }
}
