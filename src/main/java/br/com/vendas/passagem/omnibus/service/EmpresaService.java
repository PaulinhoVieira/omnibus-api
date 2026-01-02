package br.com.vendas.passagem.omnibus.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    public EmpresaService(EmpresaRepository empresaRepository, UsuarioRepository usuarioRepository, EmpresaMapper empresaMapper) {
        this.empresaRepository = empresaRepository;
        this.usuarioRepository = usuarioRepository;
        this.empresaMapper = empresaMapper;
    }

    @Transactional
    public EmpresaResponseDTO criar(EmpresaRequestDTO empresaRequestDTO) {
        Usuario usuarioDono = obterUsuarioPorId(empresaRequestDTO.usuarioDonoId());
        Empresa empresaEntity = empresaMapper.toEntity(empresaRequestDTO, usuarioDono);
        return empresaMapper.toResponse(empresaRepository.save(empresaEntity));
    }
    
    @Transactional(readOnly = true)
    public EmpresaResponseDTO obterPorIdResponseDTO(Long id) {
        return empresaMapper.toResponse(obterPorId(id));
    }

    @Transactional
    public EmpresaResponseDTO atualizar(Long id, EmpresaRequestDTO empresaAtualizada) {
        Empresa empresaExistente = obterPorId(id);
        empresaExistente.setNomeFantasia(empresaAtualizada.nomeFantasia());
        empresaExistente.setCnpj(empresaAtualizada.cnpj());
        return empresaMapper.toResponse(empresaRepository.save(empresaExistente));
    }
    
    @Transactional
    public void deletar(Long id) {
        empresaRepository.delete(obterPorId(id));
    }

    @Transactional(readOnly = true)
    public Empresa obterPorId(Long id) {
        return empresaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Empresa não encontrada com id: " + id));
    }

    @Transactional(readOnly = true)
    public Usuario obterUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado com id: " + id));
    }
}
