-- ReservaEspacosMaputo - Dados de Teste
-- Base de dados: reserva_espacos_maputo
--
-- O DataLoader insere dados automaticamente ao iniciar.
-- Use este script apenas para repor dados manualmente no MySQL.
--
-- O hash BCrypt na tabela usuarios corresponde a: engsoft2026!
-- Para gerar um novo hash: https://bcrypt-generator.com (rounds=10)

CREATE DATABASE IF NOT EXISTS reserva_espacos_maputo
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE reserva_espacos_maputo;

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE pagamentos;
TRUNCATE TABLE reservas;
TRUNCATE TABLE espacos;
TRUNCATE TABLE proprietarios;
TRUNCATE TABLE clientes;
TRUNCATE TABLE usuarios;
SET FOREIGN_KEY_CHECKS = 1;

-- Admin - senha: engsoft2026!
-- Para gerar novo hash BCrypt: https://bcrypt-generator.com
INSERT INTO usuarios (nome, email, senha, role, activo) VALUES
('Administrador do Sistema', 'admin@reservas.mz',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPVDdwejnAG',
 'ADMIN', 1);

INSERT INTO clientes (nome, apelido, telefone, bairro, tipo_documento, numero_documento) VALUES
('Armindo',   'Muchanga', '+258 84 312 4567', 'Polana Cimento', 'BI',         '1234567890MZ'),
('Esperanca', 'Guambe',   '+258 82 456 7890', 'Sommerschield',  'BI',         '0987654321MZ'),
('Helder',    'Sithole',  '+258 86 789 0123', 'Malhangalene',   'Passaporte', 'MZ0023456A'),
('Lurdes',    'Nhaca',    '+258 84 234 5678', 'Maxaquene',      'BI',         '1122334455MZ'),
('Osvaldo',   'Cossa',    '+258 82 345 6789', 'Julius Nyerere', 'BI',         '6677889900MZ');

INSERT INTO proprietarios (nome, apelido, telefone, bairro, nuit) VALUES
('Joaquim', 'Nhamithambo', '+258 84 678 9012', 'Polana Cimento', '400112233'),
('Celeste', 'Tivane',      '+258 82 789 0124', 'Julius Nyerere', '400998877');

INSERT INTO espacos (proprietario_id, nome_espaco, descricao, bairro, capacidade, preco_reserva, tipo_evento, disponibilidade) VALUES
(1, 'Salao Polana Gold',
   'Salao elegante em Polana Cimento com capacidade para 300 pessoas, ar condicionado central e sistema de som profissional.',
   'Polana Cimento', 300, 50000.00, 'Casamento', 1),
(1, 'Centro de Conferencias Sommerschield',
   'Sala moderna com projector HD, wi-fi de alta velocidade e capacidade para 150 pessoas.',
   'Sommerschield', 150, 30000.00, 'Conferencia', 1),
(2, 'Jardim dos Eventos Malhangalene',
   'Espaco ao ar livre com jardim tropical e iluminacao ambiente, ideal para festas ate 500 pessoas.',
   'Malhangalene', 500, 75000.00, 'Festa', 1),
(2, 'Auditorio Julius Nyerere',
   'Auditorio com palco profissional, iluminacao cenica e capacidade para 400 pessoas.',
   'Julius Nyerere', 400, 60000.00, 'Conferencia', 0),
(1, 'Sala VIP Executive Polana',
   'Sala executiva climatizada para reunioes e eventos corporativos de pequena dimensao.',
   'Polana Cimento', 50, 15000.00, 'Reuniao', 1);

INSERT INTO reservas (cliente_id, data_evento, espaco_id, hora_inicio, hora_fim, numero_participantes, valor_total, estado) VALUES
(1, DATE_ADD(CURDATE(), INTERVAL 10 DAY), 1, '14:00:00', '23:00:00', 250, 450000.00, 'CONFIRMADA'),
(2, DATE_ADD(CURDATE(), INTERVAL  5 DAY), 2, '09:00:00', '17:00:00', 100, 240000.00, 'PENDENTE'),
(3, DATE_ADD(CURDATE(), INTERVAL 20 DAY), 3, '18:00:00', '23:00:00', 400, 375000.00, 'PENDENTE'),
(4, DATE_ADD(CURDATE(), INTERVAL  3 DAY), 5, '10:00:00', '13:00:00',  30,  45000.00, 'CONFIRMADA'),
(5, DATE_ADD(CURDATE(), INTERVAL 15 DAY), 1, '15:00:00', '22:00:00', 200, 350000.00, 'CANCELADA');

INSERT INTO pagamentos (reserva_id, valor_pago, data_pagamento, metodo_pagamento) VALUES
(1, 450000.00, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'M-Pesa'),
(2, 120000.00, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'Transferencia Bancaria'),
(4,  45000.00, CURDATE(),                            'e-Mola');
