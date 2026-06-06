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

    // ----------------------------------------------------------------
    // GET – consultas (ADMIN + PROPRIETARIO + CLIENTE)
    // ----------------------------------------------------------------

    @Operation(
        summary = "Listar todos os espacos",
        description = "Acessivel por ADMIN, PROPRIETARIO e CLIENTE.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasAnyRole('ADMIN','PROPRIETARIO','CLIENTE')")
    @GetMapping
    public ResponseEntity<List<Espaco>> listarTodos() {
        return ResponseEntity.ok(espacoService.listarTodos());
    }

    @Operation(
        summary = "Listar espacos publicamente (sem autenticacao)",
        description = "Endpoint publico — nao requer token."
    )
    @GetMapping("/publico")
    public ResponseEntity<List<Espaco>> listarPublico() {
        return ResponseEntity.ok(espacoService.listarTodos());
    }

    @Operation(
        summary = "Consultar espaco por ID",
        description = "Acessivel por ADMIN, PROPRIETARIO e CLIENTE.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Espaco encontrado"),
        @ApiResponse(responseCode = "404", description = "Espaco nao encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN','PROPRIETARIO','CLIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<Espaco> buscarPorId(
            @Parameter(description = "ID do espaco") @PathVariable Long id) {
        return ResponseEntity.ok(espacoService.buscarPorId(id));
    }

    @Operation(
        summary = "Consultar espacos por proprietario",
        description = "Acessivel por ADMIN, PROPRIETARIO e CLIENTE.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasAnyRole('ADMIN','PROPRIETARIO','CLIENTE')")
    @GetMapping("/proprietario/{proprietarioId}")
    public ResponseEntity<List<Espaco>> porProprietario(
            @Parameter(description = "ID do proprietario") @PathVariable Long proprietarioId) {
        return ResponseEntity.ok(espacoService.buscarPorProprietario(proprietarioId));
    }

    @Operation(
        summary = "Consultar espacos por tipo de evento",
        description = "Pesquisa parcial, case-insensitive. Acessivel por ADMIN, PROPRIETARIO e CLIENTE.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasAnyRole('ADMIN','PROPRIETARIO','CLIENTE')")
    @GetMapping("/tipo/{tipoEvento}")
    public ResponseEntity<List<Espaco>> porTipoEvento(
            @Parameter(description = "Tipo de evento", example = "Casamento")
            @PathVariable String tipoEvento) {
        return ResponseEntity.ok(espacoService.buscarPorTipoEvento(tipoEvento));
    }

    // ----------------------------------------------------------------
    // POST – registar (ADMIN + PROPRIETARIO)
    // ----------------------------------------------------------------

    @Operation(
        summary = "Registar um novo espaco",
        description = "Apenas ADMIN e PROPRIETARIO podem criar espacos.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Espaco criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados invalidos"),
        @ApiResponse(responseCode = "403", description = "Sem permissao — requer ADMIN ou PROPRIETARIO"),
        @ApiResponse(responseCode = "404", description = "Proprietario nao encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN','PROPRIETARIO')")
    @PostMapping
    public ResponseEntity<Espaco> registar(@Valid @RequestBody Dtos.EspacoRequest dto) {
        return ResponseEntity.status(201).body(espacoService.registar(dto));
    }

    // ----------------------------------------------------------------
    // PUT – actualizar (ADMIN + PROPRIETARIO)
    // ----------------------------------------------------------------

    @Operation(
        summary = "Actualizar dados de um espaco",
        description = "Apenas ADMIN e PROPRIETARIO podem editar espacos.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Espaco actualizado"),
        @ApiResponse(responseCode = "403", description = "Sem permissao — requer ADMIN ou PROPRIETARIO"),
        @ApiResponse(responseCode = "404", description = "Espaco ou proprietario nao encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN','PROPRIETARIO')")
    @PutMapping("/{id}")
    public ResponseEntity<Espaco> actualizar(
            @Parameter(description = "ID do espaco") @PathVariable Long id,
            @Valid @RequestBody Dtos.EspacoRequest dto) {
        return ResponseEntity.ok(espacoService.actualizar(id, dto));
    }

    // ----------------------------------------------------------------
    // DELETE – remover (ADMIN + PROPRIETARIO)
    // ----------------------------------------------------------------

    @Operation(
        summary = "Remover um espaco",
        description = "Apenas ADMIN e PROPRIETARIO podem remover espacos.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Espaco removido com sucesso"),
        @ApiResponse(responseCode = "403", description = "Sem permissao — requer ADMIN ou PROPRIETARIO"),
        @ApiResponse(responseCode = "404", description = "Espaco nao encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN','PROPRIETARIO')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(
            @Parameter(description = "ID do espaco") @PathVariable Long id) {
        espacoService.remover(id);
        return ResponseEntity.noContent().build();
    }

    // ----------------------------------------------------------------
    // POST /{id}/foto – upload de imagem (ADMIN + PROPRIETARIO)
    // ----------------------------------------------------------------

    @Operation(
        summary = "Upload de foto do espaco",
        description = "Apenas ADMIN e PROPRIETARIO podem fazer upload de fotos.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasAnyRole('ADMIN','PROPRIETARIO')")
    @PostMapping("/{id}/foto")
    public ResponseEntity<Espaco> uploadFoto(
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

        return ResponseEntity.ok(espacoService.actualizarFoto(id, nomeFicheiro));
    }
}
