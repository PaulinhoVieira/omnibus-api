package br.com.vendas.passagem.omnibus.config.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;

import br.com.vendas.passagem.omnibus.domain.Usuario;
import br.com.vendas.passagem.omnibus.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TokenFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;

    public TokenFilter(TokenService tokenService, UsuarioRepository usuarioRepository) {
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                                            throws ServletException, IOException {

        var token = this.recoverToken(request);

        if (token != null) {
            String subject = tokenService.validateToken(token);

            if (subject != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                usuarioRepository.findById(Long.valueOf(subject)).ifPresent(usuario -> authenticate(request, usuario, token));
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null) return null; 

        return authHeader.replace("Bearer ", "");
    }

    private void authenticate(HttpServletRequest request, Usuario usuario, String token) {
        // Extrair o perfil ativo do token
        String perfilAtivo = JWT.decode(token).getClaim("perfil").asString();
        
        // Criar authority apenas com o perfil do token
        var authority = new SimpleGrantedAuthority("ROLE_" + perfilAtivo);
        
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            usuario,
            null,
            Collections.singletonList(authority)
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
