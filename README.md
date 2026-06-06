# Sistema de Gestão e Reserva de Espaços para Eventos — Maputo

> **Disciplina:** Engenharia de Software · **Curso:** ETSI 4º Ano · **Avaliação:** Segunda Avaliação  
> **Tema:** Azarias Mahumane — Desenvolvimento de Sistema de Gestão e Reserva de Espaços para Eventos na Cidade de Maputo  
> **Data de entrega:** 05/06/2026

---

## Sobre o Projecto

Sistema REST API desenvolvido em **Java + Spring Boot** para gerir e reservar espaços para eventos que acontecem na cidade de Maputo. Suporta autenticação JWT e controlo de acesso por perfis (ADMIN, PROPRIETÁRIO, CLIENTE).

---

## Tecnologias Utilizadas

| Tecnologia | Versão |
|---|---|
| Java | 17 |
| Spring Boot | 3.2.5 |
| Spring Data JPA | 3.2.x |
| Spring Security + JWT | 3.2.x / 0.11.5 |
| MySQL | 8.x |
| Springdoc OpenAPI (Swagger) | 2.5.0 |
| Maven | 3.x |

---

## Estrutura do Projecto

```
reserva-espacos-maputo/
├── src/
│   └── main/
│       ├── java/com/reservaespacos/
│       │   ├── config/          # SecurityConfig, SwaggerConfig, DataLoader, WebConfig
│       │   ├── controller/      # AuthController, ClienteController, ProprietarioController,
│       │   │                    # EspacoController, ReservaController, PagamentoController
│       │   ├── dto/             # Dtos (LoginRequest, LoginResponse, AlterarSenhaRequest, etc.)
│       │   ├── exception/       # GlobalExceptionHandler, excepções customizadas
│       │   ├── model/           # Cliente, Proprietario, Espaco, Reserva, Pagamento, Usuario
│       │   ├── repository/      # Interfaces Spring Data JPA
│       │   ├── security/        # JwtUtils, JwtAuthFilter, UsuarioDetailsService
│       │   └── service/         # Lógica de negócio (AuthService, ClienteService, ...)
│       └── resources/
│           ├── application.properties
│           ├── dados_teste.sql
│           └── static/          # Frontend HTML/CSS/JS
└── pom.xml
```

---

## Como Executar

### Pré-requisitos
- Java 17+
- MySQL 8+
- Maven 3+

### 1. Clonar o repositório

```bash
git clone https://github.com/<SEU_USUARIO>/reserva-espacos-maputo.git
cd reserva-espacos-maputo
```

### 2. Criar a base de dados

```sql
CREATE DATABASE reserva_espacos_maputo CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Configurar credenciais MySQL via variáveis de ambiente

```bash
# Linux / macOS
export DB_USERNAME=root
export DB_PASSWORD=a_sua_senha_mysql

# Windows (PowerShell)
$env:DB_USERNAME="root"
$env:DB_PASSWORD="a_sua_senha_mysql"
```

### 4. Compilar e iniciar

```bash
mvn spring-boot:run
```

A aplicação inicia em `http://localhost:8080`.

---

## Credencial Inicial (ADMIN)

Ao iniciar pela primeira vez, é criado **apenas** o utilizador administrador:

| Perfil | Email | Senha inicial |
|---|---|---|
| ADMIN | admin@reservas.mz | **engenharia de software2026** |

> ⚠️ **IMPORTANTE:** Altere a senha imediatamente após o primeiro login via `PUT /auth/senha`.

Para criar utilizadores PROPRIETARIO e CLIENTE, utilize a API:
`POST /auth/register` (requer token ADMIN)

---

## Documentação (Swagger)

Aceder após iniciar a aplicação:

```
http://localhost:8080/swagger-ui.html
```

**Como autenticar no Swagger:**
1. Execute **POST /auth/login** com email e senha
2. Copie o valor do campo `token` da resposta
3. Clique no botão **Authorize** (cadeado) no topo da página
4. Cole o token no campo **bearerAuth** e clique "Authorize"
5. Todos os endpoints protegidos passam a funcionar

---

## Endpoints Principais

### Autenticação
| Método | Endpoint | Acesso | Descrição |
|---|---|---|---|
| POST | `/auth/login` | Público | Login — retorna token JWT |
| POST | `/auth/register` | ADMIN | Criar novo utilizador |
| PUT | `/auth/senha` | Autenticado | Alterar própria senha |
| GET | `/auth/me` | Autenticado | Perfil do utilizador actual |
| GET | `/auth/utilizadores` | ADMIN | Listar todos os utilizadores |

### Cliente
| Método | Endpoint | Acesso |
|---|---|---|
| GET | `/cliente` | ADMIN |
| GET | `/cliente/{bairro}` | ADMIN, CLIENTE |
| POST | `/cliente` | ADMIN, CLIENTE |
| PUT | `/cliente/{id}` | ADMIN, CLIENTE |
| DELETE | `/cliente/{id}` | ADMIN |

### Proprietário
| Método | Endpoint | Acesso |
|---|---|---|
| GET | `/proprietario` | ADMIN |
| GET | `/proprietario/{id}` | ADMIN, PROPRIETARIO |
| POST | `/proprietario` | ADMIN, PROPRIETARIO |
| PUT | `/proprietario/{id}` | ADMIN, PROPRIETARIO |
| DELETE | `/proprietario/{id}` | ADMIN |

### Espaço
| Método | Endpoint | Acesso |
|---|---|---|
| GET | `/espaco` | Autenticado |
| GET | `/espaco/publico` | Público |
| GET | `/espaco/{id}` | Autenticado |
| GET | `/espaco/proprietario/{proprietarioId}` | Autenticado |
| GET | `/espaco/tipo/{tipoEvento}` | Autenticado |
| POST | `/espaco` | ADMIN, PROPRIETARIO |
| PUT | `/espaco/{id}` | ADMIN, PROPRIETARIO |
| DELETE | `/espaco/{id}` | ADMIN, PROPRIETARIO |
| POST | `/espaco/{id}/foto` | ADMIN, PROPRIETARIO |

### Reserva
| Método | Endpoint | Acesso |
|---|---|---|
| GET | `/reserva` | ADMIN, PROPRIETARIO |
| GET | `/reserva/{id}` | Autenticado |
| GET | `/reserva/data/{dataEvento}` | Autenticado |
| GET | `/reserva/cliente/{clienteId}` | Autenticado |
| GET | `/reserva/espaco/{espacoId}` | Autenticado |
| POST | `/reserva` | ADMIN, CLIENTE |
| PUT | `/reserva/{id}` | ADMIN, PROPRIETARIO |

### Pagamento
| Método | Endpoint | Acesso |
|---|---|---|
| GET | `/pagamento` | ADMIN, PROPRIETARIO |
| GET | `/pagamento/{id}` | Autenticado |
| GET | `/pagamento/data/{dataPagamento}` | Autenticado |
| POST | `/pagamento` | ADMIN, CLIENTE |

---

## Estados da Reserva

```
PENDENTE → CONFIRMADA → CONCLUIDA
         → CANCELADA
```

> O sistema confirma automaticamente a reserva quando o pagamento cobre o valor total.

---

## Segurança

- Autenticação via **JWT (JSON Web Token)** — tokens com validade de **24 horas**
- Perfis: **ADMIN** (acesso total), **PROPRIETARIO** (gerir espaços e reservas), **CLIENTE** (reservas e pagamentos)
- Criação de utilizadores apenas pelo ADMIN
- Senhas encriptadas com **BCrypt**

---

## Exemplos de Uso com Postman

### 1. Login
```json
POST /auth/login
Content-Type: application/json

{
  "email": "admin@reservas.mz",
  "senha": "engenharia de software2026"
}
```

### 2. Alterar senha (obrigatório após primeiro login)
```json
PUT /auth/senha
Authorization: Bearer <token>
Content-Type: application/json

{
  "senhaActual": "engenharia de software2026",
  "novaSenha": "MinhaNovaS3nh@"
}
```

### 3. Criar utilizador PROPRIETARIO
```json
POST /auth/register
Authorization: Bearer <token-admin>
Content-Type: application/json

{
  "nome": "Joaquim Nhamithambo",
  "email": "joaquim@reservas.mz",
  "senha": "SenhaSegura123",
  "role": "PROPRIETARIO"
}
```

### 4. Registar reserva
```json
POST /reserva?clienteId=1&espacoId=1
Authorization: Bearer <token>
Content-Type: application/json

{
  "dataEvento": "2026-07-20",
  "horaInicio": "14:00:00",
  "horaFim": "22:00:00",
  "numeroParticipantes": 200
}
```

### 5. Registar pagamento
```json
POST /pagamento?reservaId=1
Authorization: Bearer <token>
Content-Type: application/json

{
  "valorPago": 400000.00,
  "dataPagamento": "2026-06-05",
  "metodoPagamento": "M-Pesa"
}
```

---

## Participação

| Nome | Participação |
|---|---|
| Azarias Mahumane | 100% |

---

## Entregáveis

- [x] Código fonte no GitHub (este repositório)
- [x] Swagger / OpenAPI em `http://localhost:8080/swagger-ui.html`
- [x] Base de dados com dados de teste (DataLoader + `dados_teste.sql`)
- [ ] Word document com Diagrama ER, exemplos de testes Postman e link do repositório

---

## Link do Repositório GitHub

https://github.com/mabasso10/reserva-espacos-maputo

> Colaborador adicionado: momademha@gmail.com

---

## Troubleshooting

### Erro: "Access Denied" (403)
- Verifique se incluiu o header `Authorization: Bearer <token>`
- Confirme que o token não expirou (validade 24h)
- Verifique se o seu perfil tem permissão para o endpoint

### Erro de conexão MySQL
- Confirme que o MySQL está em execução: `sudo systemctl status mysql`
- Verifique as variáveis de ambiente `DB_USERNAME` e `DB_PASSWORD`
- Confirme que a base de dados foi criada com `CREATE DATABASE reserva_espacos_maputo`

### Porta 8080 ocupada
```bash
# Verificar processo na porta
lsof -i :8080
# Mudar porta no application.properties
server.port=8081
```
