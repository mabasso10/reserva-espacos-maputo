package com.reservaespacos.controller;

import com.reservaespacos.dto.Dtos;
import com.reservaespacos.model.Pagamento;
import com.reservaespacos.service.PagamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/pagamento")
@Tag(name = "Pagamento", description = "Gestao de pagamentos das reservas")
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

    // ----------------------------------------------------------------
    // GET /pagamento – listar todos (ADMIN + PROPRIETARIO)
    // ----------------------------------------------------------------

    @Operation(
        summary = "Listar todos os pagamentos",
        description = "Apenas ADMIN e PROPRIETARIO podem ver todos os pagamentos.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "403", description = "Sem permissao — requer ADMIN ou PROPRIETARIO")
    @PreAuthorize("hasAnyRole('ADMIN','PROPRIETARIO')")
    @GetMapping
    public ResponseEntity<List<Pagamento>> listarTodos() {
        return ResponseEntity.ok(pagamentoService.listarTodos());
    }

    // ----------------------------------------------------------------
    // GET /pagamento/{id} (ADMIN + PROPRIETARIO + CLIENTE)
    // ----------------------------------------------------------------

    @Operation(
        summary = "Consultar pagamento por ID",
        description = "ADMIN, PROPRIETARIO e CLIENTE podem consultar um pagamento pelo ID.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pagamento encontrado"),
        @ApiResponse(responseCode = "403", description = "Sem permissao"),
        @ApiResponse(responseCode = "404", description = "Pagamento nao encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN','PROPRIETARIO','CLIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<Pagamento> buscarPorId(
            @Parameter(description = "ID do pagamento") @PathVariable Long id) {
        return ResponseEntity.ok(pagamentoService.buscarPorId(id));
    }

    // ----------------------------------------------------------------
    // GET /pagamento/data/{data} (ADMIN + PROPRIETARIO)
    // ----------------------------------------------------------------

    @Operation(
        summary = "Consultar pagamentos por data",
        description = "Formato da data: yyyy-MM-dd. Apenas ADMIN e PROPRIETARIO.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pagamentos encontrados"),
        @ApiResponse(responseCode = "403", description = "Sem permissao — requer ADMIN ou PROPRIETARIO"),
        @ApiResponse(responseCode = "404", description = "Nenhum pagamento na data indicada")
    })
    @PreAuthorize("hasAnyRole('ADMIN','PROPRIETARIO')")
    @GetMapping("/data/{dataPagamento}")
    public ResponseEntity<List<Pagamento>> porData(
            @Parameter(description = "Data do pagamento (yyyy-MM-dd)", example = "2026-06-01")
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataPagamento) {
        return ResponseEntity.ok(pagamentoService.buscarPorData(dataPagamento));
    }

    // ----------------------------------------------------------------
    // POST /pagamento – registar (ADMIN + CLIENTE)
    // ----------------------------------------------------------------

    @Operation(
        summary = "Registar um pagamento",
        description = "Apenas ADMIN e CLIENTE podem registar pagamentos. "
                    + "Se o valor pago cobrir o total da reserva, esta e automaticamente CONFIRMADA.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pagamento registado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados invalidos ou reserva cancelada"),
        @ApiResponse(responseCode = "403", description = "Sem permissao — requer ADMIN ou CLIENTE"),
        @ApiResponse(responseCode = "404", description = "Reserva nao encontrada")
    })
    @PreAuthorize("hasAnyRole('ADMIN','CLIENTE')")
    @PostMapping
    public ResponseEntity<Pagamento> registar(
            @Valid @RequestBody Dtos.PagamentoRequest dto,
            @Parameter(description = "ID da reserva", required = true)
            @RequestParam Long reservaId) {
        return ResponseEntity.status(201).body(
            pagamentoService.registar(dto, reservaId)
        );
    }
}
