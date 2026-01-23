package br.com.vendas.passagem.omnibus.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.vendas.passagem.omnibus.domain.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    @Query("select u from Usuario u where u.email = :login")
    UserDetails findByLogin(@Param("login") String login);
    Optional<Usuario> findByEmail(String email);
}
