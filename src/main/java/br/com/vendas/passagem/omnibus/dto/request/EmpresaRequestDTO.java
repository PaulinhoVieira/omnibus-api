package br.com.vendas.passagem.omnibus.dto.request;

import org.hibernate.validator.constraints.br.CNPJ;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EmpresaRequestDTO(
    @NotBlank(message = "O CNPJ é obrigatório")
    @CNPJ(message = "CNPJ inválido")
    String cnpj,

    @NotBlank(message = "O nome fantasia é obrigatório")
    String nomeFantasia,

    String razaoSocial,

    @NotNull(message = "O ID do usuário dono é obrigatório")
    Long usuarioDonoId
) {}
