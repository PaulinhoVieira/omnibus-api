package br.com.vendas.passagem.omnibus.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.vendas.passagem.omnibus.annotation.Auditable;
import br.com.vendas.passagem.omnibus.domain.Usuario;
import br.com.vendas.passagem.omnibus.domain.enums.TipoPerfil;
import br.com.vendas.passagem.omnibus.dto.mapper.UsuarioMapper;
import br.com.vendas.passagem.omnibus.dto.request.AuthenticationDTO;
import br.com.vendas.passagem.omnibus.dto.request.UsuarioRequestDTO;
import br.com.vendas.passagem.omnibus.dto.response.UsuarioResponseDTO;
import br.com.vendas.passagem.omnibus.repository.UsuarioRepository;
import br.com.vendas.passagem.omnibus.config.security.TokenService;

@Service
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper, 
                         PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager,
                         TokenService tokenService) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
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

    @Transactional(readOnly = true)
    public String autenticar(AuthenticationDTO authRequest) {
        // 1. Buscar usuário
        Usuario usuario = usuarioRepository.findByEmailWithEmpresas(authRequest.email())
                .orElseThrow(() -> new BadCredentialsException("Credenciais inválidas"));

        // 2. Validar se o usuário possui o perfil solicitado
        if (!usuario.possuiPerfil(authRequest.perfilDesejado())) {
            throw new BadCredentialsException("Você não possui permissão para acessar como " + authRequest.perfilDesejado());
        }

        // 3. Se o perfil for EMPRESA, validar se tem empresa associada
        if (authRequest.perfilDesejado() == TipoPerfil.EMPRESA && 
            (usuario.getEmpresas() == null || usuario.getEmpresas().isEmpty())) {
            throw new BadCredentialsException("Você não possui empresa cadastrada");
        }

        // 4. Autenticar credenciais
        var usernamePassword = new UsernamePasswordAuthenticationToken(
            authRequest.email(), 
            authRequest.senha()
        );
        var authentication = authenticationManager.authenticate(usernamePassword);

        // 5. Gerar e retornar token
        return tokenService.gerarToken((Usuario) authentication.getPrincipal(), authRequest.perfilDesejado());
    }
}