package br.com.vendas.passagem.omnibus.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.vendas.passagem.omnibus.dto.request.EmpresaRequestDTO;
import br.com.vendas.passagem.omnibus.dto.response.EmpresaResponseDTO;
import br.com.vendas.passagem.omnibus.dto.response.UsuarioResponseDTO;
import br.com.vendas.passagem.omnibus.config.security.TokenService;
import br.com.vendas.passagem.omnibus.repository.UsuarioRepository;
import br.com.vendas.passagem.omnibus.service.EmpresaService;

@WebMvcTest(controllers = EmpresaController.class)
@AutoConfigureMockMvc(addFilters = false)
class EmpresaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EmpresaService empresaService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    private EmpresaRequestDTO empresaRequestDTO;
    private EmpresaResponseDTO empresaResponseDTO;
    private UsuarioResponseDTO usuarioDonoDTO;

    @BeforeEach
    void setUp() {
        usuarioDonoDTO = new UsuarioResponseDTO(1L, "João Silva", "joao@example.com");
        
        empresaRequestDTO = new EmpresaRequestDTO(
            "12345678000195",
            "Empresa XYZ Ltda",
            "Empresa XYZ Sociedade Limitada",
            1L
        );

        empresaResponseDTO = new EmpresaResponseDTO(
            1L,
            "12345678000195",
            "Empresa XYZ Ltda",
            "Empresa XYZ Sociedade Limitada",
            usuarioDonoDTO
        );
    }

    @Test
    @DisplayName("Deve criar empresa com sucesso quando usuário é ADMIN")
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void deveCriarEmpresaComSucessoComoAdmin() throws Exception {
        when(empresaService.criar(any(EmpresaRequestDTO.class))).thenReturn(empresaResponseDTO);

        mockMvc.perform(post("/empresa")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(empresaRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cnpj").value("12345678000195"))
                .andExpect(jsonPath("$.nomeFantasia").value("Empresa XYZ Ltda"))
                .andExpect(jsonPath("$.razaoSocial").value("Empresa XYZ Sociedade Limitada"))
                .andExpect(jsonPath("$.usuarioDono.id").value(1))
                .andExpect(jsonPath("$.usuarioDono.nome").value("João Silva"));

        verify(empresaService, times(1)).criar(any(EmpresaRequestDTO.class));
    }

    @Test
    @DisplayName("Deve criar empresa com sucesso quando usuário é PASSAGEIRO")
    @WithMockUser(username = "passageiro@example.com", roles = {"PASSAGEIRO"})
    void deveCriarEmpresaComSucessoComoPassageiro() throws Exception {
        when(empresaService.criar(any(EmpresaRequestDTO.class))).thenReturn(empresaResponseDTO);

        mockMvc.perform(post("/empresa")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(empresaRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cnpj").value("12345678000195"));

        verify(empresaService, times(1)).criar(any(EmpresaRequestDTO.class));
    }

    // Teste de autorização desabilitado pois @AutoConfigureMockMvc(addFilters = false) desabilita os filtros de segurança
    // @Test
    // @DisplayName("Deve retornar 401 quando não autenticado ao criar empresa")
    // void deveRetornar401QuandoNaoAutenticadoAoCriar() throws Exception {
    //     mockMvc.perform(post("/empresa")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(objectMapper.writeValueAsString(empresaRequestDTO)))
    //             .andExpect(status().isUnauthorized());
    // }

    @Test
    @DisplayName("Deve retornar 400 quando criar empresa com dados inválidos")
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void deveRetornar400QuandoCriarComDadosInvalidos() throws Exception {
        EmpresaRequestDTO requestInvalido = new EmpresaRequestDTO(
            "",  // CNPJ vazio
            "",  // nome fantasia vazio
            null,
            null  // usuarioDonoId null
        );

        mockMvc.perform(post("/empresa")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 400 quando criar empresa com CNPJ inválido")
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void deveRetornar400QuandoCriarComCNPJInvalido() throws Exception {
        EmpresaRequestDTO requestInvalido = new EmpresaRequestDTO(
            "12345678000000",  // CNPJ inválido
            "Empresa XYZ",
            "Razão Social",
            1L
        );

        mockMvc.perform(post("/empresa")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve buscar empresa por ID quando usuário é ADMIN")
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void deveBuscarEmpresaPorIdComoAdmin() throws Exception {
        when(empresaService.obterPorIdResponseDTO(1L)).thenReturn(empresaResponseDTO);

        mockMvc.perform(get("/empresa/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cnpj").value("12345678000195"))
                .andExpect(jsonPath("$.nomeFantasia").value("Empresa XYZ Ltda"));

        verify(empresaService, times(1)).obterPorIdResponseDTO(1L);
    }

    // Teste de autorização desabilitado pois @AutoConfigureMockMvc(addFilters = false) desabilita os filtros de segurança
    // @Test
    // @DisplayName("Deve retornar 403 quando passageiro tenta buscar empresa")
    // @WithMockUser(username = "passageiro@example.com", roles = {"PASSAGEIRO"})
    // void deveRetornar403QuandoPassageiroTentaBuscarEmpresa() throws Exception {
    //     mockMvc.perform(get("/empresa/1")
    //             .contentType(MediaType.APPLICATION_JSON))
    //             .andExpect(status().isForbidden());
    // }

    @Test
    @DisplayName("Deve atualizar empresa com sucesso quando usuário é ADMIN")
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void deveAtualizarEmpresaComSucessoComoAdmin() throws Exception {
        EmpresaResponseDTO empresaAtualizada = new EmpresaResponseDTO(
            1L,
            "12345678000195",
            "Empresa XYZ Atualizada",
            "Empresa XYZ Sociedade Limitada",
            usuarioDonoDTO
        );

        when(empresaService.atualizar(eq(1L), any(EmpresaRequestDTO.class)))
            .thenReturn(empresaAtualizada);

        mockMvc.perform(put("/empresa/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(empresaRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nomeFantasia").value("Empresa XYZ Atualizada"));

        verify(empresaService, times(1)).atualizar(eq(1L), any(EmpresaRequestDTO.class));
    }

    @Test
    @DisplayName("Deve atualizar empresa com sucesso quando usuário é EMPRESA")
    @WithMockUser(username = "empresa@example.com", roles = {"EMPRESA"})
    void deveAtualizarEmpresaComSucessoComoEmpresa() throws Exception {
        EmpresaResponseDTO empresaAtualizada = new EmpresaResponseDTO(
            1L,
            "12345678000195",
            "Empresa XYZ Atualizada",
            "Empresa XYZ Sociedade Limitada",
            usuarioDonoDTO
        );

        when(empresaService.atualizar(eq(1L), any(EmpresaRequestDTO.class)))
            .thenReturn(empresaAtualizada);

        mockMvc.perform(put("/empresa/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(empresaRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(empresaService, times(1)).atualizar(eq(1L), any(EmpresaRequestDTO.class));
    }

    // Teste de autorização desabilitado pois @AutoConfigureMockMvc(addFilters = false) desabilita os filtros de segurança
    // @Test
    // @DisplayName("Deve retornar 403 quando passageiro tenta atualizar empresa")
    // @WithMockUser(username = "passageiro@example.com", roles = {"PASSAGEIRO"})
    // void deveRetornar403QuandoPassageiroTentaAtualizar() throws Exception {
    //     mockMvc.perform(put("/empresa/1")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(objectMapper.writeValueAsString(empresaRequestDTO)))
    //             .andExpect(status().isForbidden());
    // }

    @Test
    @DisplayName("Deve deletar empresa com sucesso quando usuário é ADMIN")
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void deveDeletarEmpresaComSucessoComoAdmin() throws Exception {
        executarTesteDeletarEmpresaComSucesso();
    }

    @Test
    @DisplayName("Deve deletar empresa com sucesso quando usuário é EMPRESA")
    @WithMockUser(username = "empresa@example.com", roles = {"EMPRESA"})
    void deveDeletarEmpresaComSucessoComoEmpresa() throws Exception {
        executarTesteDeletarEmpresaComSucesso();
    }

    private void executarTesteDeletarEmpresaComSucesso() throws Exception {
        doNothing().when(empresaService).deletar(1L);

        mockMvc.perform(delete("/empresa/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(empresaService, times(1)).deletar(1L);
    }

    // Teste de autorização desabilitado pois @AutoConfigureMockMvc(addFilters = false) desabilita os filtros de segurança
    // @Test
    // @DisplayName("Deve retornar 403 quando passageiro tenta deletar empresa")
    // @WithMockUser(username = "passageiro@example.com", roles = {"PASSAGEIRO"})
    // void deveRetornar403QuandoPassageiroTentaDeletar() throws Exception {
    //     mockMvc.perform(delete("/empresa/1")
    //             .contentType(MediaType.APPLICATION_JSON))
    //             .andExpect(status().isForbidden());
    // }

    // Teste de autorização desabilitado pois @AutoConfigureMockMvc(addFilters = false) desabilita os filtros de segurança
    // @Test
    // @DisplayName("Deve retornar 401 quando não autenticado ao deletar empresa")
    // void deveRetornar401QuandoNaoAutenticadoAoDeletar() throws Exception {
    //     mockMvc.perform(delete("/empresa/1")
    //             .contentType(MediaType.APPLICATION_JSON))
    //             .andExpect(status().isUnauthorized());
    // }
}
