package br.com.vendas.passagem.omnibus.dto.request;

import br.com.vendas.passagem.omnibus.domain.enums.TipoPerfil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthenticationDTO(
    @NotBlank @Email String email,
    @NotBlank String senha,
    @NotNull TipoPerfil perfilDesejado

) {}
