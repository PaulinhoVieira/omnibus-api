package br.com.vendas.passagem.omnibus.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.vendas.passagem.omnibus.domain.Usuario;
import br.com.vendas.passagem.omnibus.domain.enums.TipoPerfil;
import br.com.vendas.passagem.omnibus.dto.request.AuthenticationDTO;
import br.com.vendas.passagem.omnibus.dto.request.UsuarioRequestDTO;
import br.com.vendas.passagem.omnibus.repository.UsuarioRepository;
import br.com.vendas.passagem.omnibus.service.UsuarioService;
import br.com.vendas.passagem.omnibus.config.security.TokenService;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/auth")
public class AuthenticationController {

    private final UsuarioRepository usuarioRepository;

    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;
    private final TokenService tokenService;

    public AuthenticationController(AuthenticationManager authenticationManager, UsuarioService usuarioService, UsuarioRepository usuarioRepository, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthenticationDTO authRequest) {
        // Buscar usuário para validar perfil
        Usuario usuario = usuarioRepository.findByEmail(authRequest.email())
                .orElseThrow(() -> new BadCredentialsException("Credenciais inválidas"));

        // Validar se o usuário possui o perfil solicitado
        if (!usuario.possuiPerfil(authRequest.perfilDesejado())) {
            throw new BadCredentialsException("Você não possui permissão para acessar como " + authRequest.perfilDesejado());
        }

        // Se o perfil for EMPRESA, validar se tem empresa associada
        if (authRequest.perfilDesejado() == TipoPerfil.EMPRESA && (usuario.getEmpresas() == null || usuario.getEmpresas().isEmpty())) {
            throw new BadCredentialsException("Você não possui empresa cadastrada");
        }

        // Autenticar
        var usernamePassword = new UsernamePasswordAuthenticationToken(authRequest.email(), authRequest.senha());
        var authentication = authenticationManager.authenticate(usernamePassword);

        String token = tokenService.gerarToken((Usuario) authentication.getPrincipal(), authRequest.perfilDesejado());

        return ResponseEntity.ok(token);
    }
    
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody UsuarioRequestDTO request) {
        if(usuarioRepository.findByEmail(request.email()).isPresent()) {
            throw new BadCredentialsException("Usuário já existe com o email: " + request.email());
        }
        
        // Service vai criptografar a senha, adicionar perfil PASSAGEIRO e garantir a persistência
        usuarioService.criarUser(request);
        return ResponseEntity.ok().build();
    }

}
