package com.reservaespacos.controller;

import com.reservaespacos.dto.Dtos;
import com.reservaespacos.model.Usuario;
import com.reservaespacos.repository.ClienteRepository;
import com.reservaespacos.repository.ProprietarioRepository;
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
    name = "Autenticacao",
    description = """
        Endpoints de autenticacao e gestao de utilizadores.

        **Fluxo de autenticacao:**
        1. Chame `POST /auth/login` com email e senha.
        2. Copie o campo `token` da resposta.
        3. No Swagger, clique em **Authorize** (cadeado no topo) e cole o token.
        4. Todas as chamadas seguintes incluirao automaticamente `Authorization: Bearer <token>`.

        **Credenciais iniciais:**
        | Perfil | Email | Senha |
        |--------|-------|-------|
        | ADMIN | admin@reservas.mz | engsoft2026! |
        """
)
public class AuthController {

    @Autowired private AuthService authService;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private ProprietarioRepository proprietarioRepository;

    @Operation(summary = "Login - obter token JWT")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login bem-sucedido - token JWT retornado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Dtos.LoginResponse.class),
                examples = @ExampleObject(name = "Login como ADMIN", value = """
                    {
                      "token": "eyJhbGciOiJIUzI1NiJ9...",
                      "tipo": "Bearer",
                      "email": "admin@reservas.mz",
                      "nome": "Administrador do Sistema",
                      "role": "ADMIN"
                    }
                    """))),
        @ApiResponse(responseCode = "401", description = "Credenciais invalidas"),
        @ApiResponse(responseCode = "400", description = "Dados invalidos")
    })
    @PostMapping("/login")
    public ResponseEntity<Dtos.LoginResponse> login(@Valid @RequestBody Dtos.LoginRequest request) {
        String token = authService.login(request.getEmail(), request.getSenha());
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail()).orElseThrow();

        Dtos.LoginResponse resp = new Dtos.LoginResponse(
            token, usuario.getEmail(), usuario.getNome(), usuario.getRole().name()
        );

        // Correlação por usuario_id — fiável, sem depender de nomes
        if (usuario.getRole() == Usuario.Role.CLIENTE) {
            clienteRepository.findByUsuarioId(usuario.getId())
                .ifPresent(c -> resp.clienteId = c.getId());
        } else if (usuario.getRole() == Usuario.Role.PROPRIETARIO) {
            proprietarioRepository.findByUsuarioId(usuario.getId())
                .ifPresent(p -> resp.proprietarioId = p.getId());
        }

        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "Registar conta (auto-registo)",
        description = """
            Cria uma nova conta de utilizador.

            **Roles permitidos:** `CLIENTE` ou `PROPRIETARIO`.
            Tentativas de criar `ADMIN` por este endpoint sao automaticamente convertidas para `CLIENTE`.
            """)
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Conta criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados invalidos ou email ja registado")
    })
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

    @Operation(summary = "Registar utilizador (ADMIN only)", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/register")
    public ResponseEntity<Dtos.RegistoResponse> adminRegister(
            @Valid @RequestBody Dtos.RegistoRequest request) {
        Usuario criado = authService.registarPublico(request);
        return ResponseEntity.status(201).body(
            new Dtos.RegistoResponse(criado.getId(), criado.getNome(), criado.getEmail(), criado.getRole().name())
        );
    }

    @Operation(summary = "Alterar senha", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/senha")
    public ResponseEntity<?> alterarSenha(@Valid @RequestBody Dtos.AlterarSenhaRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        authService.alterarSenha(auth.getName(), request.getSenhaActual(), request.getNovaSenha());
        return ResponseEntity.ok(java.util.Map.of("mensagem", "Senha alterada com sucesso."));
    }

    @Operation(summary = "Listar todos os utilizadores (ADMIN)", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/utilizadores")
    public ResponseEntity<?> listarUtilizadores() {
        return ResponseEntity.ok(
            usuarioRepository.findAll().stream()
                .peek(u -> u.setSenha(null))
                .toList()
        );
    }

    @Operation(summary = "Perfil do utilizador actual", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/me")
    public ResponseEntity<?> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return usuarioRepository.findByEmail(auth.getName())
            .map(u -> { u.setSenha(null); return ResponseEntity.ok(u); })
            .orElse(ResponseEntity.notFound().build());
    }
}
