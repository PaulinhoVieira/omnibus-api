package br.com.vendas.passagem.omnibus.dto.response;


public record EmpresaResponseDTO(
    Long id,
    String cnpj,
    String nomeFantasia,
    String razaoSocial,
    UsuarioResponseDTO usuarioDono
) {}
