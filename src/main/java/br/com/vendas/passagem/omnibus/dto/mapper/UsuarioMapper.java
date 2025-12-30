package br.com.vendas.passagem.omnibus.dto.mapper;

import org.springframework.stereotype.Component;

import br.com.vendas.passagem.omnibus.domain.Usuario;
import br.com.vendas.passagem.omnibus.dto.request.UsuarioRequestDTO;
import br.com.vendas.passagem.omnibus.dto.response.UsuarioResponseDTO;

@Component
public class UsuarioMapper {
    
    public Usuario toEntity(UsuarioRequestDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenha(dto.senha()); // Lembre-se que no service você vai criptografar
        usuario.setCpf(dto.cpf());
        return usuario;
    }
    
    public UsuarioResponseDTO toDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail()
        );
    }
}
