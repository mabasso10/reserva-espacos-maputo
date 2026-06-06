package com.reservaespacos.controller;

import com.reservaespacos.model.Proprietario;
import com.reservaespacos.model.Usuario;
import com.reservaespacos.repository.UsuarioRepository;
import com.reservaespacos.service.ProprietarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/proprietario")
@Tag(name = "Proprietario", description = "Gestao de proprietarios de espacos")
public class ProprietarioController {

    @Autowired
    private ProprietarioService proprietarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Operation(summary = "Listar todos os proprietarios", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Proprietario>> listarTodos() {
        return ResponseEntity.ok(proprietarioService.listarTodos());
    }

    /**
     * GET /proprietario/me
     * Devolve o perfil de Proprietario do utilizador autenticado.
     * A ligação é feita por usuario_id — fiável e sem ambiguidade.
     */
    @Operation(
        summary = "Obter dados do proprietario autenticado",
        description = "Devolve o registo de Proprietario associado ao token JWT em uso.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Dados retornados"),
        @ApiResponse(responseCode = "404", description = "Perfil de proprietario nao encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN','PROPRIETARIO')")
    @GetMapping("/me")
    public ResponseEntity<Proprietario> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElse(null);
        if (usuario == null) return ResponseEntity.notFound().build();

        return proprietarioService.buscarPorUsuarioId(usuario.getId())
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Buscar proprietario por ID", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('ADMIN','PROPRIETARIO')")
    @GetMapping("/{id}")
    public ResponseEntity<Proprietario> buscarPorId(
            @Parameter(description = "ID do proprietario") @PathVariable Long id) {
        return ResponseEntity.ok(proprietarioService.buscarPorId(id));
    }

    /**
     * POST /proprietario
     * Ao registar, associa automaticamente o Usuario autenticado ao perfil.
     */
    @Operation(
        summary = "Registar um novo proprietario",
        description = "Cria o perfil de Proprietario e associa-o ao utilizador autenticado.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Proprietario criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados invalidos ou NUIT duplicado"),
        @ApiResponse(responseCode = "403", description = "Sem permissao")
    })
    @PreAuthorize("hasAnyRole('ADMIN','PROPRIETARIO')")
    @PostMapping
    public ResponseEntity<Proprietario> registar(@Valid @RequestBody Proprietario proprietario) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = usuarioRepository.findByEmail(auth.getName()).orElse(null);
        if (usuario != null) {
            proprietario.setUsuario(usuario);
        }
        return ResponseEntity.status(201).body(proprietarioService.registar(proprietario));
    }

    @Operation(summary = "Actualizar dados de um proprietario", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('ADMIN','PROPRIETARIO')")
    @PutMapping("/{id}")
    public ResponseEntity<Proprietario> actualizar(
            @Parameter(description = "ID do proprietario") @PathVariable Long id,
            @Valid @RequestBody Proprietario proprietario) {
        return ResponseEntity.ok(proprietarioService.actualizar(id, proprietario));
    }

    @Operation(summary = "Remover um proprietario", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(
            @Parameter(description = "ID do proprietario") @PathVariable Long id) {
        proprietarioService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
