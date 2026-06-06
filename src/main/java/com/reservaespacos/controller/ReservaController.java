package com.reservaespacos.controller;

import com.reservaespacos.dto.Dtos;
import com.reservaespacos.model.Reserva;
import com.reservaespacos.service.ReservaService;
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
@RequestMapping("/reserva")
@Tag(name = "Reserva", description = "Gestao de reservas de espacos")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;
    // GET /reserva – listar todas (ADMIN + PROPRIETARIO)
    

    @Operation(
        summary = "Listar todas as reservas",
        description = "Apenas ADMIN e PROPRIETARIO podem ver todas as reservas.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "403", description = "Sem permissao - requer ADMIN ou PROPRIETARIO")
    @PreAuthorize("hasAnyRole('ADMIN','PROPRIETARIO')")
    @GetMapping
    public ResponseEntity<List<Reserva>> listarTodas() {
        return ResponseEntity.ok(reservaService.listarTodas());
    }
    // GET /reserva/{id} (ADMIN + PROPRIETARIO + CLIENTE)
    

    @Operation(
        summary = "Consultar reserva por ID",
        description = "ADMIN, PROPRIETARIO e CLIENTE podem consultar uma reserva pelo ID.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reserva encontrada"),
        @ApiResponse(responseCode = "403", description = "Sem permissao"),
        @ApiResponse(responseCode = "404", description = "Reserva nao encontrada")
    })
    @PreAuthorize("hasAnyRole('ADMIN','PROPRIETARIO','CLIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<Reserva> buscarPorId(
            @Parameter(description = "ID da reserva") @PathVariable Long id) {
        return ResponseEntity.ok(reservaService.buscarPorId(id));
    }
    // GET /reserva/data/{data} (ADMIN + PROPRIETARIO)
    

    @Operation(
        summary = "Consultar reservas por data do evento",
        description = "Formato da data: yyyy-MM-dd. Acessivel por ADMIN e PROPRIETARIO.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasAnyRole('ADMIN','PROPRIETARIO')")
    @GetMapping("/data/{dataEvento}")
    public ResponseEntity<List<Reserva>> porData(
            @Parameter(description = "Data do evento (yyyy-MM-dd)", example = "2026-07-15")
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataEvento) {
        return ResponseEntity.ok(reservaService.buscarPorDataEvento(dataEvento));
    }
    // GET /reserva/cliente/{clienteId} (ADMIN + PROPRIETARIO + CLIENTE)
    

    @Operation(
        summary = "Consultar reservas por cliente",
        description = "ADMIN, PROPRIETARIO e CLIENTE podem consultar reservas de um cliente.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasAnyRole('ADMIN','PROPRIETARIO','CLIENTE')")
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Reserva>> porCliente(
            @Parameter(description = "ID do cliente") @PathVariable Long clienteId) {
        return ResponseEntity.ok(reservaService.buscarPorCliente(clienteId));
    }
    // GET /reserva/espaco/{espacoId} (ADMIN + PROPRIETARIO)
    

    @Operation(
        summary = "Consultar reservas por espaco",
        description = "Apenas ADMIN e PROPRIETARIO podem ver as reservas de um espaco.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasAnyRole('ADMIN','PROPRIETARIO')")
    @GetMapping("/espaco/{espacoId}")
    public ResponseEntity<List<Reserva>> porEspaco(
            @Parameter(description = "ID do espaco") @PathVariable Long espacoId) {
        return ResponseEntity.ok(reservaService.buscarPorEspaco(espacoId));
    }
    // POST /reserva – criar reserva (ADMIN + CLIENTE)
    

    @Operation(
        summary = "Registar uma nova reserva",
        description = "Apenas ADMIN e CLIENTE podem criar reservas. "
                    + "O valor total e calculado automaticamente. O estado inicial e sempre PENDENTE.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Reserva criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados invalidos ou espaco indisponivel"),
        @ApiResponse(responseCode = "403", description = "Sem permissao - requer ADMIN ou CLIENTE"),
        @ApiResponse(responseCode = "404", description = "Cliente ou espaco nao encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN','CLIENTE')")
    @PostMapping
    public ResponseEntity<Reserva> registar(
            @Valid @RequestBody Dtos.ReservaRequest dto,
            @Parameter(description = "ID do cliente", required = true) @RequestParam Long clienteId,
            @Parameter(description = "ID do espaco",  required = true) @RequestParam Long espacoId) {
        return ResponseEntity.status(201).body(
            reservaService.registar(dto, clienteId, espacoId)
        );
    }
    // PUT /reserva/{id} – actualizar estado (ADMIN + PROPRIETARIO)
    

    @Operation(
        summary = "Actualizar estado de uma reserva",
        description = "Apenas ADMIN e PROPRIETARIO podem confirmar, cancelar ou concluir reservas. "
                    + "Estados validos: PENDENTE, CONFIRMADA, CANCELADA, CONCLUIDA.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Estado invalido"),
        @ApiResponse(responseCode = "403", description = "Sem permissao - requer ADMIN ou PROPRIETARIO"),
        @ApiResponse(responseCode = "404", description = "Reserva nao encontrada")
    })
    @PreAuthorize("hasAnyRole('ADMIN','PROPRIETARIO')")
    @PutMapping("/{id}")
    public ResponseEntity<Reserva> atualizarEstado(
            @Parameter(description = "ID da reserva") @PathVariable Long id,
            @Valid @RequestBody Dtos.AtualizarEstadoReservaRequest request) {
        return ResponseEntity.ok(reservaService.atualizarEstado(id, request.getEstado()));
    }
}
