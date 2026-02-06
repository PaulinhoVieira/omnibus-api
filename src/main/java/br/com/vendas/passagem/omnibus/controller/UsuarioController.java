package br.com.vendas.passagem.omnibus.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import br.com.vendas.passagem.omnibus.domain.Usuario;
import br.com.vendas.passagem.omnibus.domain.enums.TipoDocumento;
import br.com.vendas.passagem.omnibus.dto.request.UsuarioRequestDTO;
import br.com.vendas.passagem.omnibus.dto.response.DocumentoResponseDTO;
import br.com.vendas.passagem.omnibus.dto.response.UsuarioResponseDTO;
import br.com.vendas.passagem.omnibus.service.DocumentoService;
import br.com.vendas.passagem.omnibus.service.UsuarioService;
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
@RequestMapping("/usuario")
@Tag(name = "Usuários", description = "API para gerenciamento de usuários")
public class UsuarioController {
    
    private final UsuarioService usuarioService;
    private final DocumentoService documentoService;

    public UsuarioController(UsuarioService usuarioService, DocumentoService documentoService) {
        this.usuarioService = usuarioService;
        this.documentoService = documentoService;
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obter usuário logado")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuário retornado",
            content = @Content(schema = @Schema(implementation = UsuarioResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> obterUsuarioLogado(@AuthenticationPrincipal Usuario usuarioLogado) {
        UsuarioResponseDTO usuario = usuarioService.obterDTOporId(usuarioLogado.getId());
        return ResponseEntity.ok(usuario);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Buscar usuário por ID")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuário encontrado",
            content = @Content(schema = @Schema(implementation = UsuarioResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Não autenticado"),
        @ApiResponse(responseCode = "403", description = "Sem permissão"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(
            @Parameter(description = "ID do usuário") @PathVariable Long id) {
        UsuarioResponseDTO usuario = usuarioService.obterDTOporId(id);
        return ResponseEntity.ok(usuario);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PASSAGEIRO')")
    @Operation(summary = "Atualizar usuário")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuário atualizado",
            content = @Content(schema = @Schema(implementation = UsuarioResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "401", description = "Não autenticado"),
        @ApiResponse(responseCode = "403", description = "Sem permissão"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(
            @Parameter(description = "ID do usuário") @PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDTO request) {
        UsuarioResponseDTO atualizado = usuarioService.atualizarUser(id, request);
        return ResponseEntity.ok(atualizado);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PASSAGEIRO')")
    @Operation(summary = "Deletar usuário")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Usuário deletado"),
        @ApiResponse(responseCode = "401", description = "Não autenticado"),
        @ApiResponse(responseCode = "403", description = "Sem permissão"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do usuário") @PathVariable Long id) {
        usuarioService.deletarUser(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PASSAGEIRO')")
    @Operation(summary = "Upload de documento")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Documento enviado",
            content = @Content(schema = @Schema(implementation = DocumentoResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "401", description = "Não autenticado"),
        @ApiResponse(responseCode = "403", description = "Sem permissão"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PostMapping(path = "/{id}/documentos", consumes = {"multipart/form-data"})
    public ResponseEntity<DocumentoResponseDTO> uploadDocumento(
            @Parameter(description = "ID do usuário") @PathVariable Long id,
            @Parameter(description = "Tipo do documento") @RequestParam("tipo") TipoDocumento tipoDocumento,
            @Parameter(description = "Arquivo a ser enviado") @RequestPart("arquivo") MultipartFile arquivo) {
        DocumentoResponseDTO salvo = documentoService.uploadDocumento(id, tipoDocumento, arquivo);
        return ResponseEntity.ok(salvo);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Alternar perfil ativo", description = "Atualiza o perfil ativo e retorna um novo token JWT.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Token JWT atualizado",
            content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "401", description = "Não autenticado"),
        @ApiResponse(responseCode = "403", description = "Sem permissão"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PostMapping("/perfil/alternar/{nomePerfil}")
    public ResponseEntity<String> alternarPerfilAtivo(
            @Parameter(description = "Perfil desejado") @PathVariable String nomePerfil,
            @AuthenticationPrincipal Usuario usuarioLogado) {
        String token = usuarioService.alternarPerfilAtivo(usuarioLogado, nomePerfil);
        return ResponseEntity.ok(token);
    }
}
