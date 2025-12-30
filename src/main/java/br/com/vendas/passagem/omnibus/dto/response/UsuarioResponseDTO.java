package br.com.vendas.passagem.omnibus.dto.response;


public record UsuarioResponseDTO(
    Long id,
    String nome,
    String email
) {}
