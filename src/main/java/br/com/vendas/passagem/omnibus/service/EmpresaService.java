package br.com.vendas.passagem.omnibus.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.vendas.passagem.omnibus.annotation.Auditable;
import br.com.vendas.passagem.omnibus.domain.Empresa;
import br.com.vendas.passagem.omnibus.domain.Usuario;
import br.com.vendas.passagem.omnibus.dto.mapper.EmpresaMapper;
import br.com.vendas.passagem.omnibus.dto.request.EmpresaRequestDTO;
import br.com.vendas.passagem.omnibus.dto.response.EmpresaResponseDTO;
import br.com.vendas.passagem.omnibus.repository.EmpresaRepository;
import br.com.vendas.passagem.omnibus.repository.UsuarioRepository;

@Service
public class EmpresaService {
    
    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpresaMapper empresaMapper;
    private final UsuarioService usuarioService;

    public EmpresaService(EmpresaRepository empresaRepository, UsuarioRepository usuarioRepository, EmpresaMapper empresaMapper, UsuarioService usuarioService) {
        this.empresaRepository = empresaRepository;
        this.usuarioRepository = usuarioRepository;
        this.empresaMapper = empresaMapper;
        this.usuarioService = usuarioService;
    }

    @Transactional
    @Auditable(action = "CREATE", entity = "Empresa")
    public EmpresaResponseDTO criar(EmpresaRequestDTO empresaRequestDTO) {
        Usuario usuarioDono = this.obterUsuarioPorId(empresaRequestDTO.usuarioDonoId());
        Empresa empresaEntity = empresaMapper.toEntity(empresaRequestDTO, usuarioDono);
        Empresa empresaSalva = empresaRepository.save(empresaEntity);
        
        // Promover usuário para perfil EMPRESA
        usuarioService.promoverParaEmpresa(empresaRequestDTO.usuarioDonoId());
        
        return empresaMapper.toResponse(empresaSalva);
    }
    
    @Transactional(readOnly = true)
    @Auditable(action = "READ", entity = "Empresa")
    public EmpresaResponseDTO obterPorIdResponseDTO(Long id) {
        return empresaMapper.toResponse(obterPorId(id));
    }

    @Transactional
    @Auditable(action = "UPDATE", entity = "Empresa")
    public EmpresaResponseDTO atualizar(Long id, EmpresaRequestDTO empresaAtualizada) {
        Empresa empresaExistente = this.obterPorId(id);
        empresaExistente.setNomeFantasia(empresaAtualizada.nomeFantasia());
        empresaExistente.setCnpj(empresaAtualizada.cnpj());
        return empresaMapper.toResponse(empresaRepository.save(empresaExistente));
    }
    
    @Transactional
    @Auditable(action = "DELETE", entity = "Empresa")
    public void deletar(Long id) {
        empresaRepository.delete(this.obterPorId(id));
    }

    public Empresa obterPorId(Long id) {
        return empresaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Empresa não encontrada com id: " + id));
    }

    public Usuario obterUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado com id: " + id));
    }
}
