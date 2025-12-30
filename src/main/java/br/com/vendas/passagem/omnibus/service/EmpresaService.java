package br.com.vendas.passagem.omnibus.service;

import org.springframework.stereotype.Service;

import br.com.vendas.passagem.omnibus.domain.Empresa;
import br.com.vendas.passagem.omnibus.domain.Usuario;
import br.com.vendas.passagem.omnibus.dto.mapper.EmpresaMapper;
import br.com.vendas.passagem.omnibus.dto.request.EmpresaRequestDTO;
import br.com.vendas.passagem.omnibus.dto.response.EmpresaResponseDTO;
import br.com.vendas.passagem.omnibus.repository.EmpresaRepository;
import br.com.vendas.passagem.omnibus.repository.UsuarioRepository;

@Service
public class EmpresaService {
    
    private EmpresaRepository empresaRepository;
    private UsuarioRepository usuarioRepository;
    private EmpresaMapper empresaMapper;


    public EmpresaService(EmpresaRepository empresaRepository, EmpresaMapper empresaMapper) {
        this.empresaRepository = empresaRepository;
        this.empresaMapper = empresaMapper;
    }

    public EmpresaResponseDTO criar(EmpresaRequestDTO empresaRequestDTO) {
        Usuario usuarioDono = obterUsuarioPorId(empresaRequestDTO.usuarioDonoId()); //TODO: na verdade vai criar o usuario vai ter que pesistir depois
        Empresa empresaEntity = empresaMapper.toEntity(empresaRequestDTO, usuarioDono);
        return empresaMapper.toResponse(empresaRepository.save(empresaEntity));
    }
    
    public EmpresaResponseDTO obterPorIdResponseDTO(Long id) {
        return empresaMapper.toResponse(obterPorId(id));
    }

    public EmpresaResponseDTO atualizar(Long id, EmpresaRequestDTO empresaAtualizada) {
        Empresa empresaExistente = obterPorId(id);
        empresaExistente.setNomeFantasia(empresaAtualizada.nomeFantasia());
        empresaExistente.setCnpj(empresaAtualizada.cnpj());
        return empresaMapper.toResponse(empresaRepository.save(empresaExistente));
    }
    
    public void deletar(Long id) {
        empresaRepository.delete(obterPorId(id));
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
