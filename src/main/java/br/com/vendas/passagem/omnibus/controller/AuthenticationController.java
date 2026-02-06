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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "API para login e registro")
public class AuthenticationController {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;

    public AuthenticationController(UsuarioService usuarioService, UsuarioRepository usuarioRepository) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
    }

    @Operation(summary = "Login", description = "Autentica o usuário e retorna um token JWT com o perfil solicitado.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Token JWT gerado",
            content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
        @ApiResponse(responseCode = "403", description = "Perfil não permitido")
    })
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthenticationDTO authRequest) {
        String token = usuarioService.autenticar(authRequest);
        return ResponseEntity.ok(token);
    }
    
    @Operation(summary = "Registro", description = "Cria um novo usuário com perfil PASSAGEIRO.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuário criado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "409", description = "Usuário já existe")
    })
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
