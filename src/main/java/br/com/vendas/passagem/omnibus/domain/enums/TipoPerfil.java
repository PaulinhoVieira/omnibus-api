package br.com.vendas.passagem.omnibus.domain.enums;

public enum TipoPerfil {
    PASSAGEIRO,
    EMPRESA,
    ADMIN;

    private String role;

    TipoPerfil() {
        this.role = this.name();
    }

    public String getRole() {
        return role;
    }
}
