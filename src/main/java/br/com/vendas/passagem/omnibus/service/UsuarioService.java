package br.com.vendas.passagem.omnibus.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.vendas.passagem.omnibus.annotation.Auditable;
import br.com.vendas.passagem.omnibus.domain.Usuario;
import br.com.vendas.passagem.omnibus.domain.enums.TipoPerfil;
import br.com.vendas.passagem.omnibus.dto.mapper.UsuarioMapper;
import br.com.vendas.passagem.omnibus.dto.request.UsuarioRequestDTO;
import br.com.vendas.passagem.omnibus.dto.response.UsuarioResponseDTO;
import br.com.vendas.passagem.omnibus.repository.UsuarioRepository;

@Service
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Auditable(action = "CREATE", entity = "Usuario")
    public UsuarioResponseDTO criarUser(UsuarioRequestDTO usuario) {
        Usuario userEntity = usuarioMapper.toEntity(usuario);
        // Criptografar senha antes de salvar
        userEntity.setSenha(passwordEncoder.encode(usuario.senha()));
        return usuarioMapper.toDTO(usuarioRepository.save(userEntity));
    }

    @Transactional(readOnly = true)
    @Auditable(action = "READ", entity = "Usuario")
    public UsuarioResponseDTO obterDTOporId(Long id) {
        return usuarioMapper.toDTO(obterPorId(id));
    }

    @Transactional
    @Auditable(action = "UPDATE", entity = "Usuario")
    public UsuarioResponseDTO atualizarUser(Long id, UsuarioRequestDTO usuarioAtualizado) {
        Usuario usuarioExistente = obterPorId(id);
        usuarioExistente.setNome(usuarioAtualizado.nome());
        usuarioExistente.setEmail(usuarioAtualizado.email());
        usuarioExistente.setSenha(passwordEncoder.encode(usuarioAtualizado.senha()));
        usuarioExistente.setCpf(usuarioAtualizado.cpf());
        return usuarioMapper.toDTO(usuarioRepository.save(usuarioExistente));
    }

    @Transactional
    @Auditable(action = "DELETE", entity = "Usuario")
    public void deletarUser(Long id) {
        usuarioRepository.delete(obterPorId(id));
    }

    public Usuario obterPorId(Long id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado com id: " + id));
    }

    // Método para adicionar perfil EMPRESA quando usuário criar empresa
    @Transactional
    public void promoverParaEmpresa(Long usuarioId) {
        Usuario usuario = obterPorId(usuarioId);
        usuario.adicionarPerfil(TipoPerfil.EMPRESA);
        usuarioRepository.save(usuario);
    }
}
