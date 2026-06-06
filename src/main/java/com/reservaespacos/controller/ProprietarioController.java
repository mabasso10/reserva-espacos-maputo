package com.reservaespacos.controller;

import com.reservaespacos.model.Proprietario;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/proprietario")
@Tag(name = "Proprietario", description = "Gestao de proprietarios de espacos")
public class ProprietarioController {

    @Autowired
    private ProprietarioService proprietarioService;

    // ----------------------------------------------------------------
    // GET /proprietario – listar todos (apenas ADMIN)
    // ----------------------------------------------------------------

    @Operation(
        summary = "Listar todos os proprietarios",
        description = "Apenas ADMIN pode ver a lista completa de proprietarios.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de proprietarios retornada com sucesso"),
        @ApiResponse(responseCode = "403", description = "Sem permissao — requer ADMIN")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Proprietario>> listarTodos() {
        return ResponseEntity.ok(proprietarioService.listarTodos());
    }

    // ----------------------------------------------------------------
    // GET /proprietario/{id} (ADMIN + PROPRIETARIO)
    // ----------------------------------------------------------------

    @Operation(
        summary = "Buscar proprietario por ID",
        description = "ADMIN pode consultar qualquer proprietario. PROPRIETARIO pode consultar os seus proprios dados.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Proprietario encontrado"),
        @ApiResponse(responseCode = "403", description = "Sem permissao — requer ADMIN ou PROPRIETARIO"),
        @ApiResponse(responseCode = "404", description = "Proprietario nao encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN','PROPRIETARIO')")
    @GetMapping("/{id}")
    public ResponseEntity<Proprietario> buscarPorId(
            @Parameter(description = "ID do proprietario") @PathVariable Long id) {
        return ResponseEntity.ok(proprietarioService.buscarPorId(id));
    }

    // ----------------------------------------------------------------
    // POST /proprietario – registar (ADMIN + PROPRIETARIO)
    // ----------------------------------------------------------------

    @Operation(
        summary = "Registar um novo proprietario",
        description = "ADMIN e PROPRIETARIO podem criar um registo de proprietario.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Proprietario criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados invalidos ou NUIT duplicado"),
        @ApiResponse(responseCode = "403", description = "Sem permissao — requer ADMIN ou PROPRIETARIO")
    })
    @PreAuthorize("hasAnyRole('ADMIN','PROPRIETARIO')")
    @PostMapping
    public ResponseEntity<Proprietario> registar(@Valid @RequestBody Proprietario proprietario) {
        return ResponseEntity.status(201).body(proprietarioService.registar(proprietario));
    }

    // ----------------------------------------------------------------
    // PUT /proprietario/{id} – actualizar (ADMIN + PROPRIETARIO)
    // ----------------------------------------------------------------

    @Operation(
        summary = "Actualizar dados de um proprietario",
        description = "ADMIN pode actualizar qualquer proprietario. PROPRIETARIO pode actualizar os seus proprios dados.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Proprietario actualizado"),
        @ApiResponse(responseCode = "403", description = "Sem permissao — requer ADMIN ou PROPRIETARIO"),
        @ApiResponse(responseCode = "404", description = "Proprietario nao encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN','PROPRIETARIO')")
    @PutMapping("/{id}")
    public ResponseEntity<Proprietario> actualizar(
            @Parameter(description = "ID do proprietario") @PathVariable Long id,
            @Valid @RequestBody Proprietario proprietario) {
        return ResponseEntity.ok(proprietarioService.actualizar(id, proprietario));
    }

    // ----------------------------------------------------------------
    // DELETE /proprietario/{id} – remover (apenas ADMIN)
    // ----------------------------------------------------------------

    @Operation(
        summary = "Remover um proprietario",
        description = "Apenas ADMIN pode remover proprietarios.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Proprietario removido com sucesso"),
        @ApiResponse(responseCode = "403", description = "Sem permissao — requer ADMIN"),
        @ApiResponse(responseCode = "404", description = "Proprietario nao encontrado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(
            @Parameter(description = "ID do proprietario") @PathVariable Long id) {
        proprietarioService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
