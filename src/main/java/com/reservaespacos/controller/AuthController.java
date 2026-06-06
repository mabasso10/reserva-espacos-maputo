package com.reservaespacos.controller;

import com.reservaespacos.dto.Dtos;
import com.reservaespacos.model.Usuario;
import com.reservaespacos.repository.UsuarioRepository;
import com.reservaespacos.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(
    name = "Autenticação",
    description = """
        Endpoints de autenticação e gestão de utilizadores.

        **Fluxo de autenticação:**
        1. Chame `POST /auth/login` com email e senha.
        2. Copie o campo `token` da resposta.
        3. No Swagger, clique em **Authorize** (cadeado no topo) e cole o token.
        4. Todas as chamadas seguintes incluirão automaticamente `Authorization: Bearer <token>`.

        **Credenciais de teste iniciais:**
        | Perfil | Email | Senha |
        |--------|-------|-------|
        | ADMIN | admin@reservas.mz | ALTERE_ESTA_SENHA |
        """
)
public class AuthController {

    @Autowired private AuthService authService;
    @Autowired private UsuarioRepository usuarioRepository;

    // ================================================================
    // POST /auth/login — PÚBLICO
    // ================================================================

    @Operation(
        summary = "Login — obter token JWT",
        description = """
            Autentica o utilizador com email e senha.
            Devolve um token JWT que deve ser usado no header `Authorization: Bearer <token>`
            em todos os pedidos seguintes a endpoints protegidos.

            **Perfis disponíveis:** ADMIN · PROPRIETARIO · CLIENTE
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Login bem-sucedido — token JWT retornado",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Dtos.LoginResponse.class),
                examples = @ExampleObject(
                    name = "Login como ADMIN",
                    summary = "Resposta de login com sucesso",
                    value = """
                        {
                          "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkByZXNlcnZhcy5teiIsImlhdCI6MTcxNzAwMDAwMCwiZXhwIjoxNzE3MDg2NDAwfQ.abc123",
                          "tipo": "Bearer",
                          "email": "admin@reservas.mz",
                          "nome": "Administrador do Sistema",
                          "role": "ADMIN"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Credenciais inválidas — email ou senha incorrectos",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    value = """
                        {
                          "timestamp": "2026-06-06T10:00:00",
                          "status": 401,
                          "error": "Unauthorized",
                          "message": "Bad credentials"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos — campos obrigatórios em falta",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    value = """
                        {
                          "timestamp": "2026-06-06T10:00:00",
                          "status": 400,
                          "errors": {
                            "email": "Email e obrigatorio",
                            "senha": "Senha e obrigatoria"
                          }
                        }
                        """
                )
            )
        )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = Dtos.LoginRequest.class),
            examples = {
                @ExampleObject(
                    name = "ADMIN",
                    summary = "Login como Administrador",
                    value = """
                        {
                          "email": "admin@reservas.mz",
                          "senha": "ALTERE_ESTA_SENHA"
                        }
                        """
                ),
                @ExampleObject(
                    name = "PROPRIETARIO",
                    summary = "Login como Proprietário",
                    value = """
                        {
                          "email": "proprietario@reservas.mz",
                          "senha": "senha12345"
                        }
                        """
                ),
                @ExampleObject(
                    name = "CLIENTE",
                    summary = "Login como Cliente",
                    value = """
                        {
                          "email": "cliente@reservas.mz",
                          "senha": "senha12345"
                        }
                        """
                )
            }
        )
    )
    @PostMapping("/login")
    public ResponseEntity<Dtos.LoginResponse> login(@Valid @RequestBody Dtos.LoginRequest request) {
        String token = authService.login(request.getEmail(), request.getSenha());
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail()).orElseThrow();
        return ResponseEntity.ok(new Dtos.LoginResponse(
            token,
            usuario.getEmail(),
            usuario.getNome(),
            usuario.getRole().name()
        ));
    }

    // ================================================================
    // POST /auth/register — PÚBLICO (CLIENTE ou PROPRIETARIO apenas)
    // ================================================================

    @Operation(
        summary = "Registar conta (auto-registo)",
        description = """
            Cria uma nova conta de utilizador.

            **Roles permitidos:** `CLIENTE` ou `PROPRIETARIO`.
            Tentativas de criar `ADMIN` por este endpoint são automaticamente convertidas para `CLIENTE`.

            Para criar um utilizador `ADMIN`, use `POST /auth/admin/register` com token de ADMIN.
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Conta criada com sucesso",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    value = """
                        {
                          "id": 2,
                          "nome": "Armindo Muchanga",
                          "email": "armindo@email.com",
                          "role": "CLIENTE"
                        }
                        """
                )
            )
        ),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou email já registado"),
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = {
                @ExampleObject(
                    name = "Registar CLIENTE",
                    value = """
                        {
                          "nome": "Armindo Muchanga",
                          "email": "armindo@email.com",
                          "senha": "senha12345",
                          "role": "CLIENTE"
                        }
                        """
                ),
                @ExampleObject(
                    name = "Registar PROPRIETARIO",
                    value = """
                        {
                          "nome": "Celeste Tivane",
                          "email": "celeste@email.com",
                          "senha": "senha12345",
                          "role": "PROPRIETARIO"
                        }
                        """
                )
            }
        )
    )
    @PostMapping("/register")
    public ResponseEntity<Dtos.RegistoResponse> register(
            @Valid @RequestBody Dtos.RegistoRequest request) {

        if (request.getRole() == null || request.getRole().equalsIgnoreCase("ADMIN")) {
            request.setRole("CLIENTE");
        }

        Usuario criado = authService.registarPublico(request);
        return ResponseEntity.status(201).body(
            new Dtos.RegistoResponse(criado.getId(), criado.getNome(), criado.getEmail(), criado.getRole().name())
        );
    }

    // ================================================================
    // POST /auth/admin/register — apenas ADMIN
    // ================================================================

    @Operation(
        summary = "Registar utilizador (ADMIN only)",
        description = """
            Cria um utilizador com qualquer role: `ADMIN`, `PROPRIETARIO` ou `CLIENTE`.

            **Requer token de ADMIN.** Use o botão **Authorize** no topo para inserir o token.
            """,
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Utilizador criado com sucesso",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    value = """
                        {
                          "id": 3,
                          "nome": "Novo Admin",
                          "email": "novo.admin@reservas.mz",
                          "role": "ADMIN"
                        }
                        """
                )
            )
        ),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou email já registado"),
        @ApiResponse(responseCode = "403", description = "Sem permissão — requer ADMIN")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = @ExampleObject(
                name = "Criar ADMIN",
                value = """
                    {
                      "nome": "Novo Administrador",
                      "email": "novo.admin@reservas.mz",
                      "senha": "senha12345",
                      "role": "ADMIN"
                    }
                    """
            )
        )
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/register")
    public ResponseEntity<Dtos.RegistoResponse> adminRegister(
            @Valid @RequestBody Dtos.RegistoRequest request) {
        Usuario criado = authService.registarPublico(request);
        return ResponseEntity.status(201).body(
            new Dtos.RegistoResponse(criado.getId(), criado.getNome(), criado.getEmail(), criado.getRole().name())
        );
    }

    // ================================================================
    // PUT /auth/senha — utilizador autenticado
    // ================================================================

    @Operation(
        summary = "Alterar senha",
        description = "Permite ao utilizador autenticado alterar a sua própria senha. Requer token válido.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Senha alterada com sucesso",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(value = """
                    { "mensagem": "Senha alterada com sucesso." }
                    """)
            )
        ),
        @ApiResponse(responseCode = "400", description = "Senha actual incorrecta ou nova senha inválida"),
        @ApiResponse(responseCode = "401", description = "Token inválido ou expirado")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = @ExampleObject(
                value = """
                    {
                      "senhaActual": "ALTERE_ESTA_SENHA",
                      "novaSenha": "NovaSenha2026!"
                    }
                    """
            )
        )
    )
    @PutMapping("/senha")
    public ResponseEntity<?> alterarSenha(@Valid @RequestBody Dtos.AlterarSenhaRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        authService.alterarSenha(auth.getName(), request.getSenhaActual(), request.getNovaSenha());
        return ResponseEntity.ok(java.util.Map.of("mensagem", "Senha alterada com sucesso."));
    }

    // ================================================================
    // GET /auth/utilizadores — apenas ADMIN
    // ================================================================

    @Operation(
        summary = "Listar todos os utilizadores (ADMIN)",
        description = "Devolve a lista completa de utilizadores registados no sistema. Apenas ADMIN tem acesso.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de utilizadores",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    value = """
                        [
                          {
                            "id": 1,
                            "nome": "Administrador do Sistema",
                            "email": "admin@reservas.mz",
                            "role": "ADMIN",
                            "activo": true
                          },
                          {
                            "id": 2,
                            "nome": "Armindo Muchanga",
                            "email": "armindo@email.com",
                            "role": "CLIENTE",
                            "activo": true
                          }
                        ]
                        """
                )
            )
        ),
        @ApiResponse(responseCode = "403", description = "Sem permissão — requer ADMIN")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/utilizadores")
    public ResponseEntity<?> listarUtilizadores() {
        return ResponseEntity.ok(
            usuarioRepository.findAll().stream()
                .peek(u -> u.setSenha(null))
                .toList()
        );
    }

    // ================================================================
    // GET /auth/me — qualquer utilizador autenticado
    // ================================================================

    @Operation(
        summary = "Perfil do utilizador actual",
        description = "Devolve os dados do utilizador cujo token está a ser usado. Qualquer role pode aceder.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Dados do utilizador autenticado",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    value = """
                        {
                          "id": 1,
                          "nome": "Administrador do Sistema",
                          "email": "admin@reservas.mz",
                          "role": "ADMIN",
                          "activo": true
                        }
                        """
                )
            )
        ),
        @ApiResponse(responseCode = "401", description = "Token inválido ou em falta")
    })
    @GetMapping("/me")
    public ResponseEntity<?> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return usuarioRepository.findByEmail(auth.getName())
            .map(u -> { u.setSenha(null); return ResponseEntity.ok(u); })
            .orElse(ResponseEntity.notFound().build());
    }
}
