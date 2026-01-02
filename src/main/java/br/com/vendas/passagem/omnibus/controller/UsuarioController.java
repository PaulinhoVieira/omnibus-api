package br.com.vendas.passagem.omnibus.controller;

import org.springframework.http.ResponseEntity;
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

import br.com.vendas.passagem.omnibus.domain.enums.TipoDocumento;
import br.com.vendas.passagem.omnibus.dto.request.UsuarioRequestDTO;
import br.com.vendas.passagem.omnibus.dto.response.DocumentoResponseDTO;
import br.com.vendas.passagem.omnibus.dto.response.UsuarioResponseDTO;
import br.com.vendas.passagem.omnibus.service.DocumentoService;
import br.com.vendas.passagem.omnibus.service.UsuarioService;
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

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criar(@Valid @RequestBody UsuarioRequestDTO request) {
        UsuarioResponseDTO created = usuarioService.criarUser(request);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        UsuarioResponseDTO usuario = usuarioService.obterDTOporId(id);
        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO request) {
        UsuarioResponseDTO atualizado = usuarioService.atualizarUser(id, request);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        usuarioService.deletarUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/{id}/documentos", consumes = {"multipart/form-data"})
    public ResponseEntity<DocumentoResponseDTO> uploadDocumento(
            @PathVariable Long id,
            @RequestParam("tipo") TipoDocumento tipoDocumento,
            @RequestPart("arquivo") MultipartFile arquivo) {
        DocumentoResponseDTO salvo = documentoService.uploadDocumento(id, tipoDocumento, arquivo);
        return ResponseEntity.ok(salvo);
    }
}
