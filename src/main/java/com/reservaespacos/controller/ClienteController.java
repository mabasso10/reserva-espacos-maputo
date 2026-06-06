package com.reservaespacos.controller;

import com.reservaespacos.model.Cliente;
import com.reservaespacos.service.ClienteService;
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
@RequestMapping("/cliente")
@Tag(name = "Cliente", description = "Gestao de clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    // ----------------------------------------------------------------
    // GET /cliente – listar todos (apenas ADMIN)
    // ----------------------------------------------------------------

    @Operation(
        summary = "Listar todos os clientes",
        description = "Apenas ADMIN pode ver a lista completa de clientes.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso"),
        @ApiResponse(responseCode = "403", description = "Sem permissao — requer ADMIN")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Cliente>> listarTodos() {
        return ResponseEntity.ok(clienteService.listarTodos());
    }

    // ----------------------------------------------------------------
    // GET /cliente/{id} (ADMIN + CLIENTE)
    // ----------------------------------------------------------------

    @Operation(
        summary = "Consultar cliente por ID",
        description = "ADMIN pode consultar qualquer cliente. CLIENTE pode consultar os seus proprios dados.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
        @ApiResponse(responseCode = "403", description = "Sem permissao — requer ADMIN ou CLIENTE"),
        @ApiResponse(responseCode = "404", description = "Cliente nao encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN','CLIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> porId(
            @Parameter(description = "ID do cliente") @PathVariable Long id) {
        return ResponseEntity.ok(clienteService.buscarPorId(id));
    }

    // ----------------------------------------------------------------
    // GET /cliente/bairro/{bairro} (ADMIN)
    // ----------------------------------------------------------------

    @Operation(
        summary = "Consultar clientes por bairro",
        description = "Apenas ADMIN pode pesquisar clientes por bairro.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Clientes encontrados"),
        @ApiResponse(responseCode = "403", description = "Sem permissao — requer ADMIN"),
        @ApiResponse(responseCode = "404", description = "Nenhum cliente no bairro informado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/bairro/{bairro}")
    public ResponseEntity<List<Cliente>> porBairro(
            @Parameter(description = "Nome do bairro", example = "Polana")
            @PathVariable String bairro) {
        return ResponseEntity.ok(clienteService.buscarPorBairro(bairro));
    }

    // ----------------------------------------------------------------
    // POST /cliente – registar (ADMIN + CLIENTE)
    // ----------------------------------------------------------------

    @Operation(
        summary = "Registar um novo cliente",
        description = "ADMIN e CLIENTE podem criar um registo de cliente.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados invalidos ou documento duplicado"),
        @ApiResponse(responseCode = "403", description = "Sem permissao — requer ADMIN ou CLIENTE")
    })
    @PreAuthorize("hasAnyRole('ADMIN','CLIENTE')")
    @PostMapping
    public ResponseEntity<Cliente> registar(@Valid @RequestBody Cliente cliente) {
        return ResponseEntity.status(201).body(clienteService.registar(cliente));
    }

    // ----------------------------------------------------------------
    // PUT /cliente/{id} – actualizar (ADMIN + CLIENTE)
    // ----------------------------------------------------------------

    @Operation(
        summary = "Actualizar dados de um cliente",
        description = "ADMIN pode actualizar qualquer cliente. CLIENTE pode actualizar os seus proprios dados.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cliente actualizado"),
        @ApiResponse(responseCode = "403", description = "Sem permissao — requer ADMIN ou CLIENTE"),
        @ApiResponse(responseCode = "404", description = "Cliente nao encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN','CLIENTE')")
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizar(
            @Parameter(description = "ID do cliente") @PathVariable Long id,
            @Valid @RequestBody Cliente cliente) {
        return ResponseEntity.ok(clienteService.actualizar(id, cliente));
    }

    // ----------------------------------------------------------------
    // DELETE /cliente/{id} – remover (apenas ADMIN)
    // ----------------------------------------------------------------

    @Operation(
        summary = "Remover um cliente",
        description = "Apenas ADMIN pode remover clientes.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Cliente removido com sucesso"),
        @ApiResponse(responseCode = "403", description = "Sem permissao — requer ADMIN"),
        @ApiResponse(responseCode = "404", description = "Cliente nao encontrado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(
            @Parameter(description = "ID do cliente") @PathVariable Long id) {
        clienteService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
