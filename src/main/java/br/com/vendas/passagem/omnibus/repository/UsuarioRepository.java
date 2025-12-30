package br.com.vendas.passagem.omnibus.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.vendas.passagem.omnibus.domain.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
}
