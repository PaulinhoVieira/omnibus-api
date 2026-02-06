package br.com.vendas.passagem.omnibus.config.security;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;

import br.com.vendas.passagem.omnibus.exception.TokenGenerationException;
import br.com.vendas.passagem.omnibus.domain.Usuario;
import br.com.vendas.passagem.omnibus.domain.enums.TipoPerfil;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String gerarToken(Usuario usuario, TipoPerfil perfilAtivo) {
        if (!usuario.possuiPerfil(perfilAtivo)) {
            throw new IllegalArgumentException("Usuário não possui o perfil informado");
        }

        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                .withIssuer("omnibus-api")
                .withSubject(String.valueOf(usuario.getId()))
                .withClaim("email", usuario.getEmail())
                .withClaim("perfil", perfilAtivo.name())
                .withExpiresAt(expiration())
                .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new TokenGenerationException("Erro ao gerar token JWT", exception);
        }
    }

    public String gerarTokenComPerfilAtivo(Usuario usuario) {
        try {
            TipoPerfil perfilAtivo = usuario.getPerfilAtivo();
            if (!usuario.possuiPerfil(perfilAtivo)) {
                throw new IllegalArgumentException("Usuário não possui o perfil ativo configurado");
            }

            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                .withIssuer("omnibus-api")
                .withSubject(String.valueOf(usuario.getId()))
                .withClaim("email", usuario.getEmail())
                .withClaim("perfil", perfilAtivo.name())
                .withExpiresAt(expiration())
                .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new TokenGenerationException("Erro ao gerar token JWT", exception);
        }
    }

    public String validateToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                .withIssuer("omnibus-api")
                .build()
                .verify(token)
                .getSubject();
        } catch (JWTVerificationException e) {
            throw new TokenGenerationException("Token inválido ou expirado", e); // criar exetion para TokenInvalidException
        }
    }

    private Instant expiration() {
        return LocalDateTime.now().plusHours(4).toInstant(ZoneOffset.of("-03:00"));
    }
}
