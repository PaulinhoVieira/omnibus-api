package br.com.vendas.passagem.omnibus.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.vendas.passagem.omnibus.domain.Usuario;
import br.com.vendas.passagem.omnibus.dto.mapper.UsuarioMapper;
import br.com.vendas.passagem.omnibus.dto.request.UsuarioRequestDTO;
import br.com.vendas.passagem.omnibus.dto.response.UsuarioResponseDTO;
import br.com.vendas.passagem.omnibus.repository.UsuarioRepository;

@Service
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
    }

    @Transactional
    public UsuarioResponseDTO criarUser(UsuarioRequestDTO usuario) {
        Usuario userEntity = usuarioMapper.toEntity(usuario);
        return usuarioMapper.toDTO(usuarioRepository.save(userEntity));
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO obterDTOporId(Long id) {
        return usuarioMapper.toDTO(obterPorId(id));
    }

    @Transactional
    public UsuarioResponseDTO atualizarUser(Long id, UsuarioRequestDTO usuarioAtualizado) {
        Usuario usuarioExistente = obterPorId(id);
        usuarioExistente.setNome(usuarioAtualizado.nome());
        usuarioExistente.setEmail(usuarioAtualizado.email());
        usuarioExistente.setSenha(usuarioAtualizado.senha());//TODO: criptografar depois que por o spring security
        usuarioExistente.setCpf(usuarioAtualizado.cpf());
        return usuarioMapper.toDTO(usuarioRepository.save(usuarioExistente));
    }

    @Transactional
    public void deletarUser(Long id) {
        usuarioRepository.delete(obterPorId(id));
    }

    @Transactional(readOnly = true)
    public Usuario obterPorId(Long id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado com id: " + id));
    } 
}
