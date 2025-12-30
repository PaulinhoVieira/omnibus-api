package br.com.vendas.passagem.omnibus.dto.request;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioRequestDTO(
    @NotBlank(message = "Nome não pode ser vazio") String nome,
    @NotBlank @Email String email,
    @NotBlank(message = "Senha não pode ser vazia") String senha,
    @NotBlank @CPF String cpf
) {}
