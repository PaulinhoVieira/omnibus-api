package br.com.vendas.passagem.omnibus.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.vendas.passagem.omnibus.dto.request.EmpresaRequestDTO;
import br.com.vendas.passagem.omnibus.dto.response.EmpresaResponseDTO;
import br.com.vendas.passagem.omnibus.service.EmpresaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/empresa")
@Tag(name = "Empresas", description = "API para gerenciamento de empresas")
public class EmpresaController {
    
    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }
     // par qualquer role autenticado admin e passageiro
    @PreAuthorize("hasAnyRole('ADMIN', 'PASSAGEIRO')")
    @Operation(summary = "Criar empresa", description = "Cria uma empresa para o usuário autenticado.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Empresa criada",
            content = @Content(schema = @Schema(implementation = EmpresaResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "401", description = "Não autenticado"),
        @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    @PostMapping
    public ResponseEntity<EmpresaResponseDTO> criar(@Valid @RequestBody EmpresaRequestDTO request) {
        EmpresaResponseDTO created = empresaService.criar(request);
        return ResponseEntity.ok(created);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Buscar empresa por ID")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Empresa encontrada",
            content = @Content(schema = @Schema(implementation = EmpresaResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Não autenticado"),
        @ApiResponse(responseCode = "403", description = "Sem permissão"),
        @ApiResponse(responseCode = "404", description = "Empresa não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EmpresaResponseDTO> buscarPorId(
            @Parameter(description = "ID da empresa") @PathVariable Long id) {
        EmpresaResponseDTO empresa = empresaService.obterPorIdResponseDTO(id);
        return ResponseEntity.ok(empresa);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPRESA')")
    @Operation(summary = "Atualizar empresa")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Empresa atualizada",
            content = @Content(schema = @Schema(implementation = EmpresaResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "401", description = "Não autenticado"),
        @ApiResponse(responseCode = "403", description = "Sem permissão"),
        @ApiResponse(responseCode = "404", description = "Empresa não encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EmpresaResponseDTO> atualizar(
            @Parameter(description = "ID da empresa") @PathVariable Long id,
            @Valid @RequestBody EmpresaRequestDTO request) {
        EmpresaResponseDTO atualizada = empresaService.atualizar(id, request);
        return ResponseEntity.ok(atualizada);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPRESA')")
    @Operation(summary = "Deletar empresa")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Empresa deletada"),
        @ApiResponse(responseCode = "401", description = "Não autenticado"),
        @ApiResponse(responseCode = "403", description = "Sem permissão"),
        @ApiResponse(responseCode = "404", description = "Empresa não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID da empresa") @PathVariable Long id) {
        empresaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
