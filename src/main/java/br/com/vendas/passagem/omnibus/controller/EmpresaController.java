package br.com.vendas.passagem.omnibus.controller;

import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ResponseEntity<EmpresaResponseDTO> criar(@Valid @RequestBody EmpresaRequestDTO request) {
        EmpresaResponseDTO created = empresaService.criar(request);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaResponseDTO> buscarPorId(@PathVariable Long id) {
        EmpresaResponseDTO empresa = empresaService.obterPorIdResponseDTO(id);
        return ResponseEntity.ok(empresa);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpresaResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody EmpresaRequestDTO request) {
        EmpresaResponseDTO atualizada = empresaService.atualizar(id, request);
        return ResponseEntity.ok(atualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        empresaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
