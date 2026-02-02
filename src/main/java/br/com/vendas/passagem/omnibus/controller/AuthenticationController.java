package br.com.vendas.passagem.omnibus.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;

import br.com.vendas.passagem.omnibus.dto.request.AuthenticationDTO;
import br.com.vendas.passagem.omnibus.dto.request.UsuarioRequestDTO;
import br.com.vendas.passagem.omnibus.repository.UsuarioRepository;
import br.com.vendas.passagem.omnibus.service.UsuarioService;

@Controller
@RequestMapping("/auth")
public class AuthenticationController {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;

    public AuthenticationController(UsuarioService usuarioService, UsuarioRepository usuarioRepository) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthenticationDTO authRequest) {
        String token = usuarioService.autenticar(authRequest);
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
