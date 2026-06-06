package com.reservaespacos.controller;

import com.reservaespacos.dto.Dtos;
import com.reservaespacos.model.Espaco;
import com.reservaespacos.service.EspacoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/espaco")
@Tag(name = "Espaco", description = "Gestao de espacos para eventos")
public class EspacoController {

    @Autowired
    private EspacoService espacoService;

    @Value("${app.uploads.dir:./uploads}")
    private String uploadsDir;

    // ── GET ───────────────────────────────────────────────────────────────────

    @Operation(summary = "Listar todos os espacos", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('ADMIN','PROPRIETARIO','CLIENTE')")
    @GetMapping
    public ResponseEntity<List<Espaco>> listarTodos() {
        return ResponseEntity.ok(espacoService.listarTodos());
    }

    @Operation(summary = "Listar espacos publicamente (sem autenticacao)")
    @GetMapping("/publico")
    public ResponseEntity<List<Espaco>> listarPublico() {
        return ResponseEntity.ok(espacoService.listarTodos());
    }

    @Operation(summary = "Consultar espaco por ID", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('ADMIN','PROPRIETARIO','CLIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<Espaco> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(espacoService.buscarPorId(id));
    }

    @Operation(summary = "Consultar espacos por proprietario", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('ADMIN','PROPRIETARIO','CLIENTE')")
    @GetMapping("/proprietario/{proprietarioId}")
    public ResponseEntity<List<Espaco>> porProprietario(@PathVariable Long proprietarioId) {
        return ResponseEntity.ok(espacoService.buscarPorProprietario(proprietarioId));
    }

    @Operation(summary = "Consultar espacos por tipo de evento", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('ADMIN','PROPRIETARIO','CLIENTE')")
    @GetMapping("/tipo/{tipoEvento}")
    public ResponseEntity<List<Espaco>> porTipoEvento(@PathVariable String tipoEvento) {
        return ResponseEntity.ok(espacoService.buscarPorTipoEvento(tipoEvento));
    }

    // ── POST ──────────────────────────────────────────────────────────────────

    @Operation(summary = "Registar um novo espaco", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('ADMIN','PROPRIETARIO')")
    @PostMapping
    public ResponseEntity<Espaco> registar(@Valid @RequestBody Dtos.EspacoRequest dto) {
        return ResponseEntity.status(201).body(espacoService.registar(dto));
    }

    // ── PUT ───────────────────────────────────────────────────────────────────

    @Operation(summary = "Actualizar dados de um espaco", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('ADMIN','PROPRIETARIO')")
    @PutMapping("/{id}")
    public ResponseEntity<Espaco> actualizar(@PathVariable Long id,
                                              @Valid @RequestBody Dtos.EspacoRequest dto) {
        return ResponseEntity.ok(espacoService.actualizar(id, dto));
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    @Operation(summary = "Remover um espaco", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('ADMIN','PROPRIETARIO')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        espacoService.remover(id);
        return ResponseEntity.noContent().build();
    }

    // ── FOTOS ─────────────────────────────────────────────────────────────────

    /**
     * CORRIGIDO: cada chamada ACUMULA a foto na lista (máx. 7).
     * Anteriormente sobrescrevia — agora chama adicionarFoto().
     */
    @Operation(
        summary = "Adicionar foto ao espaco (max 7)",
        description = "Cada chamada acumula uma foto. Chame este endpoint em loop para múltiplas fotos.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Foto adicionada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Limite de 7 fotos atingido ou ficheiro inválido"),
        @ApiResponse(responseCode = "413", description = "Ficheiro demasiado grande (max 10 MB)")
    })
    @PreAuthorize("hasAnyRole('ADMIN','PROPRIETARIO')")
    @PostMapping("/{id}/foto")
    public ResponseEntity<Espaco> adicionarFoto(
            @Parameter(description = "ID do espaco") @PathVariable Long id,
            @RequestParam("arquivo") MultipartFile arquivo) throws IOException {

        Path dir = Paths.get(uploadsDir);
        if (!Files.exists(dir)) Files.createDirectories(dir);

        String nomeOriginal = arquivo.getOriginalFilename() != null
                ? arquivo.getOriginalFilename() : "foto";
        String extensao = nomeOriginal.contains(".")
                ? nomeOriginal.substring(nomeOriginal.lastIndexOf(".")) : ".jpg";
        String nomeFicheiro = "espaco_" + id + "_" + UUID.randomUUID() + extensao;

        Path destino = dir.resolve(nomeFicheiro);
        Files.copy(arquivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

        // CORRIGIDO: acumula em vez de sobrescrever
        return ResponseEntity.ok(espacoService.adicionarFoto(id, nomeFicheiro));
    }

    /**
     * Remove uma foto específica da lista do espaço pelo nome do ficheiro.
     */
    @Operation(
        summary = "Remover foto do espaco",
        description = "Remove uma foto da lista pelo nome do ficheiro.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasAnyRole('ADMIN','PROPRIETARIO')")
    @DeleteMapping("/{id}/foto/{nomeFicheiro}")
    public ResponseEntity<Espaco> removerFoto(
            @PathVariable Long id,
            @PathVariable String nomeFicheiro) throws IOException {

        Espaco espaco = espacoService.removerFoto(id, nomeFicheiro);

        // Apaga o ficheiro físico do disco (best-effort)
        try {
            Path ficheiro = Paths.get(uploadsDir).resolve(nomeFicheiro);
            Files.deleteIfExists(ficheiro);
        } catch (IOException ignored) {}

        return ResponseEntity.ok(espaco);
    }
}
